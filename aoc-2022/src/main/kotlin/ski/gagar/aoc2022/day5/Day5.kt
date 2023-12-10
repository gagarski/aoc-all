package ski.gagar.aoc2022.day5

import ski.gagar.aoc.util.Puzzle

import java.io.InputStream

object Day5 : Puzzle {
    override val name: String = "Supply Stacks"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day5/crates.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day5.part1.doMoves(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day5.part2.doMoves(input.bufferedReader().lineSequence())
}