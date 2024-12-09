package ski.gagar.aoc2023.day11

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day11.part1.sumManhattan
import java.io.InputStream

const val EXPANSION_PART_2 = 1000000L
object Day11 : Puzzle {
    override val name: String = "Cosmic Expansion"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day11/galaxies.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumManhattan(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumManhattan(lines, EXPANSION_PART_2, EXPANSION_PART_2)
        }
}