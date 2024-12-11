package ski.gagar.aoc2024.day11

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream
import java.math.BigInteger

object Day11 : Puzzle {
    override val name: String = "Plutonian Pebbles"
    override fun part1(input: InputStream): Int =
        ski.gagar.aoc2024.day11.part1.countStones(input.bufferedReader().readTextAndClose(), 25)
    override fun part2(input: InputStream): BigInteger =
        ski.gagar.aoc2024.day11.part2.countStones(input.bufferedReader().readTextAndClose(), 75)
}