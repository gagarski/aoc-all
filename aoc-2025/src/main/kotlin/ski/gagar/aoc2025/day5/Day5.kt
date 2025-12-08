package ski.gagar.aoc2025.day5

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day5.part1.countFreshIngredients
import ski.gagar.aoc2025.day5.part2.countAllFreshIngredients
import java.io.InputStream

object Day5 : Puzzle {
    override val name: String = "Cafeteria"
    override fun part1(input: InputStream) = input.bufferedReader().useLines {
        countFreshIngredients(it)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines {
        countAllFreshIngredients(it)
    }
}