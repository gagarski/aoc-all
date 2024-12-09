package ski.gagar.aoc2023.day1

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day1 : Puzzle {
    override val name: String = "Trebuchet?!"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day1/calibration.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day1.part1.calibration(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day1.part2.calibration(lines)
        }
}