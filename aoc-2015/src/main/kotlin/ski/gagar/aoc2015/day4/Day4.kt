package ski.gagar.aoc2015.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "The Ideal Stocking Stuffer"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day4/coins.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day4.part1.bruteforceCoins(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day4.part2.bruteforceCoins(input.bufferedReader().readTextAndClose())
}