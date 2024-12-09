package ski.gagar.aoc2022.day10

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day10.part1.countStrength
import ski.gagar.aoc2022.day10.part2.runLoopWise
import java.io.InputStream

object Day10 : Puzzle {
    override val name: String = "Cathode-Ray Tube"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day10/cathode.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countStrength(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            runLoopWise(lines)
        }
}