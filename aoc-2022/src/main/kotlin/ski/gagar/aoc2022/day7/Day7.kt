package ski.gagar.aoc2022.day7

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day7.part1.getSumOfDirsLessThan
import ski.gagar.aoc2022.day7.part2.freeUp
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "No Space Left On Device"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day7/console.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getSumOfDirsLessThan(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            freeUp(lines)
        }
}