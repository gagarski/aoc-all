package ski.gagar.aoc2022.day1

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day1 : Puzzle {
    override val name: String = "Calorie Counting"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day1/calories.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day1.part1.calories(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day1.part2.calories(input.bufferedReader().lineSequence())
}