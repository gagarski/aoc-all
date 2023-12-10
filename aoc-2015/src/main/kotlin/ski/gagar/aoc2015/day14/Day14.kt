package ski.gagar.aoc2015.day14

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day14 : Puzzle {
    override val name: String = "Reindeer Olympics"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day14/deers.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day14.part1.runDeers(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day14.part2.runDeers(input.bufferedReader().lineSequence())
}