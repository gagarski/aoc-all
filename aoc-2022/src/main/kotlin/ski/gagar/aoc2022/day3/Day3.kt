package ski.gagar.aoc2022.day3

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day3 : Puzzle {
    override val name: String = "Rucksack Reorganization"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day3/rucksacks.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day3.part1.sumPriorities(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day3.part2.sumPriorities(lines)
        }
}