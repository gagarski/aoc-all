package ski.gagar.aoc2015.day17.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day16.part1.findFirstMatching
import java.math.BigInteger
import java.util.BitSet

data class Container(val volume: Int)


fun List<Container>.applyMask(mask: BigInteger): Int {
    require(this.size >= mask.bitLength())

    var sum = 0

    for ((ix, container) in this.withIndex()) {
        if (mask.testBit(ix)) sum += container.volume
    }

    return sum
}

fun nCombinations(containers: List<Container>, sum: Int = 150): Int {
    val mask = BitSet(containers.size)

    var i = BigInteger.ZERO

    var count = 0

    while (i != BigInteger.TWO.pow(containers.size).dec()) {
        val current = containers.applyMask(i)

        if (current == sum) {
            count++
        }

        i = i.inc()
    }

    return count
}

fun nCombinations(sequence: Sequence<String>, sum: Int = 150): Int =
    nCombinations(sequence.map { Container(it.toInt()) }.toList(), sum)

fun day17Part1() {
    println("day17/part1/containers: ${
        nCombinations(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day17/containers.txt").bufferedReader().lineSequence()
        )
    }")
}
