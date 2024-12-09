package ski.gagar.aoc2024.day7

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "Bridge Repair"
    override val inputPath: String = "/ski.gagar.aoc.aoc2024.day7/numbers.txt"

    override fun part1(input: InputStream) = ski.gagar.aoc2024.day7.part1.sumAchievable(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = ski.gagar.aoc2024.day7.part2.sumAchievable(input.bufferedReader().lineSequence())
}