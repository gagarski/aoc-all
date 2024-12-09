package ski.gagar.aoc2015.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day6.part1.nBulbs
import ski.gagar.aoc2015.day6.part2.totalBrightness
import java.io.InputStream

object Day6 : Puzzle {
    override val name: String = "Probably a Fire Hazard"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            nBulbs(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            totalBrightness(lines)
        }
}