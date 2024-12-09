package ski.gagar.aoc2023.day18

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day18 : Puzzle {
    override val name: String = "Lavaduct Lagoon"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day18/lagoon.txt"

    override fun part1(input: InputStream) = ski.gagar.aoc2023.day18.part1.getFilledArea(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = ski.gagar.aoc2023.day18.part2.getFilledArea(input.bufferedReader().readTextAndClose())
}