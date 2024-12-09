package ski.gagar.aoc2015.day2

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day2.part1.getTotalArea
import ski.gagar.aoc2015.day2.part2.getTotalRibbonLength
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Was Told There Would Be No Math"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getTotalArea(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getTotalRibbonLength(lines)
        }
}