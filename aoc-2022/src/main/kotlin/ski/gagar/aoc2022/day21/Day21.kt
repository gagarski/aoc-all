package ski.gagar.aoc2022.day21

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day21 : Puzzle {
    override val name: String = "Monkey Math"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day21/monkeys.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day21.part1.evaluate(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day21.part2.evaluate(input.bufferedReader().readTextAndClose())
}