package ski.gagar.aoc2022.day22

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day22 : Puzzle {
    override val name: String = "Monkey Map"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day22/field.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day22.part1.evaluate(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day22.part2.evaluate(input.bufferedReader().readTextAndClose())
}