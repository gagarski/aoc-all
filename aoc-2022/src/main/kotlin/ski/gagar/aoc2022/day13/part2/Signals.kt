package ski.gagar.aoc2022.day13.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day13.part1.SignalParser

private val DIVIDER_1 = SignalParser.parse("[[2]]")
private val DIVIDER_2 = SignalParser.parse("[[6]]")

fun getIndicesOfDivider(seq: Sequence<String>): Int {
    val signals = seq.filter { it.isNotBlank() }
        .map { SignalParser.parse(it) }
        .toMutableList()
    signals.add(DIVIDER_1)
    signals.add(DIVIDER_2)
    signals.sort()

    return (signals.indexOf(DIVIDER_1) + 1) * (signals.indexOf(DIVIDER_2) + 1)
}

fun day13Part2() {
    println("day13/part2/signals: ${
        getIndicesOfDivider(getResourceAsStream("/ski.gagar.aoc.aoc2022.day13/signals.txt").bufferedReader().lineSequence())
    }")
}
