package ski.gagar.aoc2023.day15

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day15.part1.sumHashesCsv
import ski.gagar.aoc2023.day15.part2.sumPowers
import java.io.InputStream

object Day15 : Puzzle {
    override val name: String = "Lens Library"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day15/hashes.txt"

    override fun part1(input: InputStream) = input.bufferedReader().readText().sumHashesCsv()
    override fun part2(input: InputStream) = sumPowers(input.bufferedReader().readText())
}