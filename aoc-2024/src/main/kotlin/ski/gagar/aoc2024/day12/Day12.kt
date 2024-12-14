package ski.gagar.aoc2024.day12

import ski.gagar.aoc.util.Puzzle

import java.io.InputStream

object Day12 : Puzzle {
    override val name = "Garden Groups"

    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day12.part1.sumFencePrices(lines)
        }

    override fun part2(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day12.part2.sumFencePrices(lines)
        }

}