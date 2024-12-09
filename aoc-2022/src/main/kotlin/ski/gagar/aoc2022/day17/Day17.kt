package ski.gagar.aoc2022.day17

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day17 : Puzzle {
    override val name: String = "Pyroclastic Flow"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day17/tetris.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day17.part1.getMaxHeight(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day17.part2.getMaxHeight(input.bufferedReader().readTextAndClose())
}