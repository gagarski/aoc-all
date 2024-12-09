package ski.gagar.aoc2023.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day12.part1.sumPatternCount
import ski.gagar.aoc2023.day12.part2.sumPatternCountFolded
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "Hot Springs"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumPatternCount(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumPatternCountFolded(lines)
        }
}