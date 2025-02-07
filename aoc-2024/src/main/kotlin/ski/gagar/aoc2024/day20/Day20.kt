package ski.gagar.aoc2024.day20

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day20 : Puzzle {
    override val name: String = "Race Condition"

    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        ski.gagar.aoc2024.day20.part1.countCheatsLongerThan(lines)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        ski.gagar.aoc2024.day20.part2.countCheatsLongerThan(lines)
    }
}