package ski.gagar.aoc2015.day13

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day13.part1.optimalHappiness
import ski.gagar.aoc2015.day13.part2.optimalHappinessWithMe
import java.io.InputStream

object Day13 : Puzzle {
    override val name: String = "Knights of the Dinner Table"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            optimalHappiness(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            optimalHappinessWithMe(lines)
        }
}