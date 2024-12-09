package ski.gagar.aoc2015.day21

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2015.day21.part1.findMinWinCost
import ski.gagar.aoc2015.day21.part2.findMaxLoseCost
import java.io.InputStream

object Day21 : Puzzle {
    override val name: String = "RPG Simulator 20XX"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day21/boss.txt"

    override fun part1(input: InputStream) = findMinWinCost(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) = findMaxLoseCost(input.bufferedReader().readTextAndClose())
}