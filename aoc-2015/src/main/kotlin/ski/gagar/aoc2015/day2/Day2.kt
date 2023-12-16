package ski.gagar.aoc2015.day2

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day2.part1.getTotalArea
import ski.gagar.aoc2015.day2.part2.getTotalRibbonLength
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Was Told There Would Be No Math"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day2/paper.txt"

    override fun part1(input: InputStream) =
        getTotalArea(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) =
        getTotalRibbonLength(input.bufferedReader().lineSequence())
}