package ski.gagar.aoc2024.day24

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2024.day24.part1.getNumberFromZ
import ski.gagar.aoc2024.day24.part2.getFaultyGates
import java.io.InputStream

object Day24 : Puzzle {
    override val name: String = "Some Assembly Required"
    override fun part1(input: InputStream) = getNumberFromZ(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = getFaultyGates(input.bufferedReader().readTextAndClose())
}