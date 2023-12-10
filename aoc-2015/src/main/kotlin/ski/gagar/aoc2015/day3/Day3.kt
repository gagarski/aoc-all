package ski.gagar.aoc2015.day3

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day3 : Puzzle {
    override val name: String= "Perfectly Spherical Houses in a Vacuum"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day3/gifts.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day3.part1.drive(
            input.bufferedReader().lineSequence().first()
        )

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day3.part2.drive(
            input.bufferedReader().lineSequence().first()
        )
}