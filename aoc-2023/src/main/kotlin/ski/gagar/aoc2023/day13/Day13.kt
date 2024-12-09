package ski.gagar.aoc2023.day13

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day13 : Puzzle {
    override val name: String = "Point of Incidence"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day13/mirrors.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day13.part1.calculateSymmetry(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day13.part2.calculateSymmetry(lines)
        }
}