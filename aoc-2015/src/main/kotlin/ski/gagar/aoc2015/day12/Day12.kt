package ski.gagar.aoc2015.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "JSAbacusFramework.io"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day12/ints.json"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day12.part1.sumIntJson(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day12.part2.sumIntJson(input.bufferedReader().readTextAndClose())
}