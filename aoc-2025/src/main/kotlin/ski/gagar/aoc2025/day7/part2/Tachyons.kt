package ski.gagar.aoc2025.day7.part2

import ski.gagar.aoc2025.day4.part1.Coordinates
import ski.gagar.aoc2025.day7.part1.Field
import java.math.BigInteger

fun Field.countTimelines(): BigInteger {
    var beams = setOf(start)
    var waysIn = mapOf(start.column to BigInteger.ONE)
    var row = start.row

    while (row < height - 1) {
        val newWaysIn = mutableMapOf<Int, BigInteger>()
        beams = sequence {
            for (newBeam in beams.asSequence().map { Coordinates(it.row + 1, it.column) }) {
                if (newBeam in splitters) {
                    val left = Coordinates(newBeam.row, newBeam.column - 1)
                    val right = Coordinates(newBeam.row, newBeam.column + 1)
                    if (left.isInside) {
                        newWaysIn[left.column] = (newWaysIn[left.column] ?: BigInteger.ZERO) + waysIn[newBeam.column]!!
                        yield(left)
                    }
                    if (right.isInside) {
                        newWaysIn[right.column] = (newWaysIn[right.column] ?: BigInteger.ZERO) + waysIn[newBeam.column]!!
                        yield(right)
                    }
                } else {
                    newWaysIn[newBeam.column] = (newWaysIn[newBeam.column] ?: BigInteger.ZERO) + waysIn[newBeam.column]!!
                    yield(newBeam)
                }
            }
        }.toSet()
        waysIn = newWaysIn
        row++
    }
    return waysIn.values.reduce(BigInteger::plus)
}