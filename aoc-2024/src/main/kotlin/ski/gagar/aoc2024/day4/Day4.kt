package ski.gagar.aoc2024.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day4.part1.countXmas
import ski.gagar.aoc2024.day4.part2.countXMas
import java.io.InputStream

object Day4 : Puzzle {
    override val name = "Ceres Search"

    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            countXmas(lines)
        }
    override fun part2(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            countXMas(lines)
        }
}