package ski.gagar.aoc2022.day8

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day8.part1.nVisible
import ski.gagar.aoc2022.day8.part2.maxScenicScore
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Treetop Tree House"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day8/forest.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            nVisible(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            maxScenicScore(lines)
        }
}