package ski.gagar.aoc2015.day5

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day5 : Puzzle {
    override val name: String = "Doesn't He Have Intern-Elves For This?"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day5/nice.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day5.part1.countNice(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day5.part2.countNice(input.bufferedReader().lineSequence())
}