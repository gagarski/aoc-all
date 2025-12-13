package ski.gagar.aoc2025.day11

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day11.part1.countPaths
import ski.gagar.aoc2025.day11.part2.countPathsIncluding
import java.io.InputStream

object Day11 : Puzzle {
    override val name: String = "Reactor"

    override fun part1(input: InputStream) = input.bufferedReader().useLines { countPaths(it) }
    override fun part2(input: InputStream) = input.bufferedReader().useLines { countPathsIncluding(it) }
}