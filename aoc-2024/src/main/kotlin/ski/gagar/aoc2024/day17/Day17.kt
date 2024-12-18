package ski.gagar.aoc2024.day17

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2024.day17.part1.cpuOutput
import ski.gagar.aoc2024.day17.part2.quineAValue
import java.io.InputStream

object Day17 : Puzzle {
    override val name: String = "Chronospatial Computer"
//    override fun part1(input: InputStream) = cpuOutput(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = quineAValue(input.bufferedReader().readTextAndClose())
}