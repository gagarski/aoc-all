package ski.gagar.aoc2024.day18

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day18.part1.shortestPathLength
import ski.gagar.aoc2024.day18.part2.cutOffAddress
import java.io.InputStream

object Day18 : Puzzle {
    override val name: String = "RAM Run"

    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            shortestPathLength(lines)
        }

    override fun part2(input: InputStream): String =
        input.bufferedReader().useLines { lines ->
            cutOffAddress(lines)
        }
}