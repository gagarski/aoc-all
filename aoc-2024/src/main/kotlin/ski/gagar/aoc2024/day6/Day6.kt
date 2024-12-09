package ski.gagar.aoc2024.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day6.part1.nVisitedCells
import ski.gagar.aoc2024.day6.part2.nLoopGens
import java.io.InputStream

object Day6 : Puzzle {
    override val name = "Guard Gallivant"
    override val inputPath = "/ski.gagar.aoc.aoc2024.day6/field.txt"

    override fun part1(input: InputStream)= nVisitedCells(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream)= nLoopGens(input.bufferedReader().lineSequence())
}