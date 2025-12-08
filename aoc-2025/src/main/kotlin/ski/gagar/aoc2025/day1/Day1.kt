package ski.gagar.aoc2025.day1

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day1 : Puzzle {
    override val name: String = "Secret Entrance"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { ski.gagar.aoc2025.day1.part1.countZeros(it) }
    override fun part2(input: InputStream) = input.bufferedReader().useLines { ski.gagar.aoc2025.day1.part2.countZeros(it) }
}