package ski.gagar.aoc2024.day7

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "Bridge Repair"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day7.part1.sumAchievable(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2024.day7.part2.sumAchievable(lines)
        }
}