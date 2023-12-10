package ski.gagar.aoc2022.day15.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day15.part1.BeaconParser
import ski.gagar.aoc2022.day15.part1.Coordinates
import java.math.BigInteger
import java.util.*

fun IntRange.coerce(interval: IntRange): IntRange? {
    val left = maxOf(this.first, interval.first)
    val right = minOf(this.last, interval.last)

    if (left > right) return null

    return left..right
}

val IntRange.length: Int
    get() = (last - first + 1) / step

class BoundedShadow(val range: IntRange, shadows: List<IntRange>, objects: List<Int>) {
    private val mergedRanges: List<IntRange>

    init {
        require(range.step == 1)
        val shadowsAndObjects = (shadows.asSequence() + objects.map { it..it }.asSequence())
            .map { it.coerce(range) }
            .filterNotNull()
            .sortedBy { it.first }
            .toList()

        require(shadowsAndObjects.isNotEmpty())

        val merged = mutableListOf<IntRange>()

        var currentStart = shadowsAndObjects.first().first
        var currentEnd = shadowsAndObjects.first().last

        for (range in shadowsAndObjects.asSequence().drop(1)) {
            if (range.first > currentEnd + 1) {
                merged.add(currentStart..currentEnd)
                currentStart = range.first
                currentEnd = range.last
            } else {
                currentEnd = maxOf(range.last, currentEnd)
            }
        }

        merged.add(currentStart..currentEnd)

        mergedRanges = merged
    }

    fun size() = range.length

    fun isFull() = mergedRanges.size == 1 && mergedRanges.first().length == size()

    fun firstUnset(): Int? {
        if (isFull()) {
            return null
        }

        return mergedRanges.first().last + 1
    }

}

class BoundedShadowOld(val range: IntRange, shadows: List<IntRange>, objects: List<Int>) {
    private val bitSet: BitSet
    private val bitSetLeftMost: Int

    init {
        val leftMostObject = objects.min()
        bitSetLeftMost = maxOf(shadows.minOf { it.first }, range.first, leftMostObject)
        bitSet = BitSet()

        for (o in objects) {
            if (o !in range) continue
            bitSet.set(o - bitSetLeftMost)
        }

        for (shadow in shadows) {
            val bounded = shadow.coerce(range) ?: continue
            bitSet.set(bounded.first - bitSetLeftMost, bounded.last - bitSetLeftMost + 1)
        }
    }

    fun size() = range.last - range.first + 1

    fun isFull() = size() == bitSet.cardinality()

    fun firstUnset(): Int? {
        if (bitSetLeftMost != range.first) {
            return bitSetLeftMost
        }

        for (i in bitSetLeftMost..range.last) {
            if (!bitSet[i]) return i
        }

        return null
    }
}

fun scan(text: String,
         xRange: IntRange = 0..4000000,
         yRange: IntRange = 2000000..4000000,
         xMul: Int = 4000000
): BigInteger? {
    val beacons = BeaconParser.parse(text)

    for (row in yRange) {
        val shadows = beacons.map { it.shadowAtRow(row) }.filterNotNull()
        val boundedShadow = BoundedShadow(
            xRange,
            shadows,
            (beacons.asSequence().map { it.sensor.x } + beacons.asSequence().map { it.beacon.x }).toList()
        )

        if (boundedShadow.isFull())
            continue

        return boundedShadow.firstUnset()!!.toBigInteger() * xMul.toBigInteger() + row.toBigInteger()
    }

    return null
}
