package ski.gagar.aoc2023.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day12.part1.sumPatternCount
import ski.gagar.aoc2023.day12.part2.sumPatternCountFolded
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "Hot Springs"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day12/springs.txt"

    override fun part1(input: InputStream) = sumPatternCount(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = sumPatternCountFolded(input.bufferedReader().lineSequence())
}