package ski.gagar.aoc2024.day19

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day19.part1.countPossibleTowels
import ski.gagar.aoc2024.day19.part2.sumCountCombos
import java.io.InputStream

object Day19 : Puzzle {
    override val name: String = "Linen Layout"

    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        countPossibleTowels(lines)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        sumCountCombos(lines)
    }
}