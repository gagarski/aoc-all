package ski.gagar.aoc2022.day1

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day1 : Puzzle {
    override val name: String = "Calorie Counting"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day1/calories.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day1.part1.calories(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day1.part2.calories(lines)
        }
}