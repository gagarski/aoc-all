package ski.gagar.aoc2023.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day4.part1.sumPoints
import ski.gagar.aoc2023.day4.part2.processPile
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "Scratchcards"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumPoints(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            processPile(lines)
        }
}