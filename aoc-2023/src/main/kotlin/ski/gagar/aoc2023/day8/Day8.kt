package ski.gagar.aoc2023.day8

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Haunted Wasteland"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day8/networks.txt"

    override fun part1(input: InputStream) = ski.gagar.aoc2023.day8.part1.nSteps(input.bufferedReader().readText())

    override fun part2(input: InputStream) = ski.gagar.aoc2023.day8.part2.nSteps(input.bufferedReader().readText())
}