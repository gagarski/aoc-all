package ski.gagar.aoc2024.day25

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day25.part1.countMatchingPairs
import java.io.InputStream

object Day25 : Puzzle {
    override val name: String = "Code Chronicle"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        countMatchingPairs(lines)
    }
}