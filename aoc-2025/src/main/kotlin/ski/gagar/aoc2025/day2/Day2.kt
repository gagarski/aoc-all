package ski.gagar.aoc2025.day2

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day2.part1.sumInvalidIds
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Gift Shop"
    override fun part1(input: InputStream) =
        sumInvalidIds(input.bufferedReader().readAllAsString())
    override fun part2(input: InputStream) =
        ski.gagar.aoc2025.day2.part2.sumInvalidIds(input.bufferedReader().readAllAsString())
}