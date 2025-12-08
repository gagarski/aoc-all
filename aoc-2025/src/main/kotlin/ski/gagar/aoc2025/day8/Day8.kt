package ski.gagar.aoc2025.day8

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day8.part1.connectJunctions
import ski.gagar.aoc2025.day8.part2.connectTillSingle
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Playground"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { connectJunctions(it) }
    override fun part2(input: InputStream) = input.bufferedReader().useLines { connectTillSingle(it) }
}