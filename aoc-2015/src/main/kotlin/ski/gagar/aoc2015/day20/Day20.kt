package ski.gagar.aoc2015.day20

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day20 : Puzzle {
    override val name: String = "Infinite Elves and Infinite Houses"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day20.part1.firstHouseWithPresentsCount(input.bufferedReader().readTextAndClose().toInt())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day20.part2.firstHouseWithPresentsCount(input.bufferedReader().readTextAndClose().toInt())
}