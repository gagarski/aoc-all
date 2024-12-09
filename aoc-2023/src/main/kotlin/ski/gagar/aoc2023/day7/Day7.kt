package ski.gagar.aoc2023.day7

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "Camel Cards"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day7/cards.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day7.part1.stakesSummary(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day7.part2.stakesSummary(lines)
        }
}