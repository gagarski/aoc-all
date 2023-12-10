package ski.gagar.aoc2022.day2

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Rock Paper Scissors"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day2/rps.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day2.part1.playAllRounds(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day2.part2.playAllRounds(input.bufferedReader().lineSequence())
}