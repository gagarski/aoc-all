package ski.gagar.aoc2015.day8

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Matchsticks"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day8.part1.countDiff(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day8.part2.countDiff(lines)
        }
}