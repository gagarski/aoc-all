package ski.gagar.aoc2024.day8

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Resonant Collinearity"
    override val inputPath: String = "/ski.gagar.aoc.aoc2024.day8/antennas.txt"

    override fun part1(input: InputStream) = ski.gagar.aoc2024.day8.part1.countAntinodes(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = ski.gagar.aoc2024.day8.part2.countAntinodes(input.bufferedReader().lineSequence())
}