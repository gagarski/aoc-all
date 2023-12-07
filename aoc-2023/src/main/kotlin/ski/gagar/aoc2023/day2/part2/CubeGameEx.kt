package ski.gagar.aoc2023.day2.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2023.day2.part1.CubeGameParser
import ski.gagar.aoc2023.day2.part1.Game
import ski.gagar.aoc2023.day2.part1.Round
import kotlin.math.round

private val Game.minSet: Round
    get() = Round(rounds.maxOf { it.red }, rounds.maxOf { it.green }, rounds.maxOf { it.blue })

private val Round.power
    get() = red * green * blue

private fun sumPowers(lines: Sequence<String>) =
    lines.map { CubeGameParser.parse(it).minSet.power }
        .sum()

fun day2Part2() {
    println(
        "day2/part2/cubes: ${
            sumPowers(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day2/cubes.txt").bufferedReader().lineSequence())
        }"
    )
}