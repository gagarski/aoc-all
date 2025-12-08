package ski.gagar.aoc2025.day3

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day2.part1.sumInvalidIds
import ski.gagar.aoc2025.day3.part1.sumMaxJoltages
import java.io.InputStream

object Day3 : Puzzle {
    override val name: String = "Lobby"
    override fun part1(input: InputStream) =
        input.bufferedReader().useLines {
            sumMaxJoltages(it)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines {
            sumMaxJoltages(it, 12)
        }
}