package ski.gagar.aoc2023.day9

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "Mirage Maintenance"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day9.part1.sumExtrapolated(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2023.day9.part2.sumExtrapolated(input.bufferedReader().lineSequence())
        }
}