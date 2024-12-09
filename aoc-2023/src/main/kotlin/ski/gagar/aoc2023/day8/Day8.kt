package ski.gagar.aoc2023.day8

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Haunted Wasteland"

    override fun part1(input: InputStream) = ski.gagar.aoc2023.day8.part1.nSteps(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) = ski.gagar.aoc2023.day8.part2.nSteps(input.bufferedReader().readTextAndClose())
}