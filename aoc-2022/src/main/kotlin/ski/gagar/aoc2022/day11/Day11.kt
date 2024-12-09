package ski.gagar.aoc2022.day11

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day11 : Puzzle {
    override val name: String = "Monkey in the Middle"

    override fun part1(input: InputStream) = ski.gagar.aoc2022.day11.part1.run(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) = ski.gagar.aoc2022.day11.part2.run(input.bufferedReader().readTextAndClose())
}