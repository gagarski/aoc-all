package ski.gagar.aoc2023.day5

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day5 : Puzzle {
    override val name: String = "If You Give A Seed A Fertilizer"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day5/locations.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2023.day5.part1.lowestLocation(input.bufferedReader().readText())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2023.day5.part2.lowestLocation(input.bufferedReader().readText())
}