package ski.gagar.aoc2024.day2

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day2 : Puzzle {
    override val name = "Red-Nosed Reports"

    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day2.part1.countSafeStrings(lines)
        }
    override fun part2(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day2.part2.countSafeStrings(lines)
        }
}