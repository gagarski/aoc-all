package ski.gagar.aoc2025.day7

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day7.part1.Field
import ski.gagar.aoc2025.day7.part2.countTimelines
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "Laboratories"
    override fun part1(input: InputStream) =
        input.bufferedReader().useLines {
            Field(it).countSplits()
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines {
            Field(it).countTimelines()
        }
}