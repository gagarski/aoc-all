package ski.gagar.aoc2022.day5

import ski.gagar.aoc.util.Puzzle

import java.io.InputStream

object Day5 : Puzzle {
    override val name: String = "Supply Stacks"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day5.part1.doMoves(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day5.part2.doMoves(lines)
        }
}