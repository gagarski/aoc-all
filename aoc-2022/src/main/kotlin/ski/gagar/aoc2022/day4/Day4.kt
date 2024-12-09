package ski.gagar.aoc2022.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day4.part1.countFullyIntersecting
import ski.gagar.aoc2022.day4.part2.countIntersecting
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "Camp Cleanup"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countFullyIntersecting(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countIntersecting(lines)
        }
}