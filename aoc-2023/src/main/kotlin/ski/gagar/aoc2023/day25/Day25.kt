package ski.gagar.aoc2023.day25

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day25.part1.biPartCount
import java.io.InputStream

object Day25 : Puzzle {
    override val name: String = "Snowverload"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day25/wires.txt"

    override fun part1(input: InputStream) = biPartCount(input.bufferedReader().lineSequence())
}