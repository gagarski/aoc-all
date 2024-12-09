package ski.gagar.aoc2015.day9

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day9.part1.shortestPath
import ski.gagar.aoc2015.day9.part2.longestPath
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "All in a Single Night"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day9/travel.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            shortestPath(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            longestPath(lines)
        }
}