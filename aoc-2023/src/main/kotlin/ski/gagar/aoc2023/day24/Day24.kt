package ski.gagar.aoc2023.day24

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day24.part1.countIntersectingInFuture
import ski.gagar.aoc2023.day24.part2.getThrowingLine
import java.io.InputStream

object Day24 : Puzzle {
    override val name: String = "Never Tell Me The Odds"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day24/lines.txt"

    override fun part1(input: InputStream) = countIntersectingInFuture(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = getThrowingLine(input.bufferedReader().lineSequence())
}