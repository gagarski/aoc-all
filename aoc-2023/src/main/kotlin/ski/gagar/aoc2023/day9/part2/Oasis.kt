package ski.gagar.aoc2023.day9.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2023.day8.part1.nSteps

class OasisSequence(seq: Sequence<Long>) {
    private val seq_ = ArrayDeque(seq.toList())

    private val seq: List<Long>
        get() = seq_.toList()

    fun extrapolate(): Long {
        val derivativesLast = mutableListOf<Long>()
        var current: List<Long> = seq_

        while (!current.isStable()) {
            current = current.derivative()
            derivativesLast.add(current.last())
        }

        for (ix in derivativesLast.lastIndex - 1 downTo 0) {
            derivativesLast[ix] = derivativesLast[ix] + derivativesLast[ix + 1]
        }
        val extrapolated = seq_.last() + (derivativesLast.firstOrNull() ?: 0)
        seq_.add(extrapolated)
        return extrapolated
    }

    fun extrapolateLeft(): Long {
        val derivativesFirst = mutableListOf<Long>()
        var current: List<Long> = seq_

        while (!current.isStable()) {
            current = current.derivative()
            derivativesFirst.add(current.first())
        }

        for (ix in derivativesFirst.lastIndex - 1 downTo 0) {
            derivativesFirst[ix] = derivativesFirst[ix] - derivativesFirst[ix + 1]
        }
        val extrapolated = seq_.first() - (derivativesFirst.firstOrNull() ?: 0)
        seq_.addFirst(extrapolated)
        return extrapolated
    }

    companion object {
        fun List<Long>.derivative() : List<Long> {
            require(size > 1)
            return this.asSequence().zipWithNext().map { (a, b) -> b - a }.toList()
        }
        fun List<Long>.isStable() = this.all { it == 0L }

        fun from(string: String) = OasisSequence(string.split(" ").asSequence().map { it.toLong() })
    }
}

fun sumExtrapolated(lines: Sequence<String>) =
    lines.map { OasisSequence.from(it).extrapolateLeft() }.sum()
