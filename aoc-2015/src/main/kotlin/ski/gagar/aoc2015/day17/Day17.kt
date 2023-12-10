package ski.gagar.aoc2015.day17

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day17 : Puzzle {
    override val name: String = "No Such Thing as Too Much"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day17/containers.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day17.part1.nCombinations(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day17.part2.nCombinations(input.bufferedReader().lineSequence())
}