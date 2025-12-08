package ski.gagar.aoc2025.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day6.part1.calculate
import ski.gagar.aoc2025.day6.part2.calculateVertical
import java.io.InputStream

object Day6 : Puzzle {
    override val name: String = "Trash Compactor"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { it -> calculate(it) }
    override fun part2(input: InputStream) = input.bufferedReader().useLines { it -> calculateVertical(it) }
}