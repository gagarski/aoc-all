package ski.gagar.aoc2015.day15

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day15 : Puzzle {
    override val name: String = "Science for Hungry People"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day15/cookies.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day15.part1.getOptimalCompoundValue(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day15.part2.getOptimalCompoundValue(lines)
        }
}