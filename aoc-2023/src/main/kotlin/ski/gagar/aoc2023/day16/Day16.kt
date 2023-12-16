package ski.gagar.aoc2023.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day16.part1.getEnergized
import ski.gagar.aoc2023.day16.part2.maxEnergizedTiles
import java.io.InputStream

object Day16 : Puzzle {
    override val name: String = "The Floor Will Be Lava"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day16/beams.txt"

    override fun part1(input: InputStream) = getEnergized(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = maxEnergizedTiles(input.bufferedReader().lineSequence())
}