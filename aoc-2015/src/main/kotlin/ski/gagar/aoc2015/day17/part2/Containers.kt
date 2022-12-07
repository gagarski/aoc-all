package ski.gagar.aoc2015.day17.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day16.part1.findFirstMatching
import java.math.BigInteger
import java.util.BitSet
import java.util.TreeMap
import java.util.TreeSet

data class Container(val volume: Int)

data class ContainerResult(val totalVolume: Int, val n: Int)

fun List<Container>.applyMask(mask: BigInteger): ContainerResult {
    require(this.size >= mask.bitLength())

    var sum = 0

    for ((ix, container) in this.withIndex()) {
        if (mask.testBit(ix)) sum += container.volume
    }

    return ContainerResult(sum, mask.bitCount())
}

fun nCombinationsMin(containers: List<Container>, sum: Int = 150): Int {
    val mask = BitSet(containers.size)

    var i = BigInteger.ZERO

    val counts = mutableMapOf<Int, Int>() // nContainers -> count

    while (i != BigInteger.TWO.pow(containers.size).dec()) {
        val (vol, n) = containers.applyMask(i)

        if (vol == sum) {
            counts[n] = (counts[n] ?: 0) + 1
        }

        i = i.inc()
    }

    return counts.minBy { (k, v) -> k }.value
}

fun nCombinations(sequence: Sequence<String>, sum: Int = 150): Int =
    nCombinationsMin(sequence.map { Container(it.toInt()) }.toList(), sum)

fun day17Part2() {
    println("day17/part1/containers: ${
        nCombinations(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day17/containers.txt").bufferedReader().lineSequence()
        )
    }")
}
