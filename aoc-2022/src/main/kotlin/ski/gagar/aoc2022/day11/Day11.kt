package ski.gagar.aoc2022.day11

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day11 : Puzzle {
    override val name: String = "Monkey in the Middle"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day11/monkeys.txt"

    override fun part1(input: InputStream) = ski.gagar.aoc2022.day11.part1.run(input.bufferedReader().readText())

    override fun part2(input: InputStream) = ski.gagar.aoc2022.day11.part2.run(input.bufferedReader().readText())
}