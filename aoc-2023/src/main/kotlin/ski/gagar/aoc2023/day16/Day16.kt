package ski.gagar.aoc2023.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day16.part1.getEnergized
import ski.gagar.aoc2023.day16.part2.maxEnergizedTiles
import java.io.InputStream

object Day16 : Puzzle {
    override val name: String = "The Floor Will Be Lava"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getEnergized(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            maxEnergizedTiles(lines)
        }
}