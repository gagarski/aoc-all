package ski.gagar.aoc2023.day3

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day3.part1.partNumbers
import ski.gagar.aoc2023.day3.part2.gearRatio
import java.io.InputStream

object Day3 : Puzzle {
    override val name: String = "Gear Ratios"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            partNumbers(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            gearRatio(lines)
        }
}