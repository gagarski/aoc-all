package ski.gagar.aoc2015.day11

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2015.day11.part1.nextCompliantPassword
import java.io.InputStream

object Day11 : Puzzle {
    override val name: String = "Corporate Policy"

    override fun part1(input: InputStream) =
        input.bufferedReader().readTextAndClose().nextCompliantPassword()

    override fun part2(input: InputStream) =
        input.bufferedReader().readTextAndClose().nextCompliantPassword().nextCompliantPassword()
}