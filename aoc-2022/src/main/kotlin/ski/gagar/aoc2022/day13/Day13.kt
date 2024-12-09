package ski.gagar.aoc2022.day13

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day13.part1.getSumOfIndices
import ski.gagar.aoc2022.day13.part2.getIndicesOfDivider
import java.io.InputStream

object Day13 : Puzzle {
    override val name: String = "Distress Signal"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getSumOfIndices(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            getIndicesOfDivider(lines)
        }
}