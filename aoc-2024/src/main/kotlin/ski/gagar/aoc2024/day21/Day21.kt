package ski.gagar.aoc2024.day21

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day21 : Puzzle {
    override val name = "Keypad Conundrum"

    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        ski.gagar.aoc2024.day21.part1.sumComplexities(lines)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        ski.gagar.aoc2024.day21.part2.sumComplexities(lines)
    }
}