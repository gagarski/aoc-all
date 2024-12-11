package ski.gagar.aoc2024.day10

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day10.part1.sumTrailHeadScores
import ski.gagar.aoc2024.day10.part2.sumTrailHeadRatings
import java.io.InputStream

object Day10 : Puzzle {
    override val name = "Hoof It"
    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumTrailHeadScores(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumTrailHeadRatings(lines)
        }
}