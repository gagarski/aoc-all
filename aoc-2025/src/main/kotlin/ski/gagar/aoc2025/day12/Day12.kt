package ski.gagar.aoc2025.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day12.part1.countWrappable
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "Christmas Tree Farm"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { countWrappable(it) }
}