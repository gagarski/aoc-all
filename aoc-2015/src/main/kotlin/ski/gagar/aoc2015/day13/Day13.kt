package ski.gagar.aoc2015.day13

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day13.part1.optimalHappiness
import ski.gagar.aoc2015.day13.part2.optimalHappinessWithMe
import java.io.InputStream

object Day13 : Puzzle {
    override val name: String = "Knights of the Dinner Table"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day13/table.txt"

    override fun part1(input: InputStream) =
        optimalHappiness(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        optimalHappinessWithMe(input.bufferedReader().lineSequence())
}