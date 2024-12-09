package ski.gagar.aoc2023.day19

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2023.day19.part1.sumTraits
import ski.gagar.aoc2023.day19.part2.nCombos
import java.io.InputStream

object Day19 : Puzzle {
    override val name: String = "Aplenty"

    override fun part1(input: InputStream) = sumTraits(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = nCombos(input.bufferedReader().readTextAndClose())
}