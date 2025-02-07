package ski.gagar.aoc2023.day20

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2023.day20.part1.countSignals
import ski.gagar.aoc2023.day20.part2.nPresses
import java.io.InputStream

object Day20 : Puzzle {
    override val name: String = "Pulse Propagation"

    override fun part1(input: InputStream) = countSignals(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = nPresses(input.bufferedReader().readTextAndClose())
}