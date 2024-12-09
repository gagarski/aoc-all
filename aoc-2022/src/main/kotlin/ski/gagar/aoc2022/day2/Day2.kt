package ski.gagar.aoc2022.day2

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Rock Paper Scissors"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day2.part1.playAllRounds(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day2.part2.playAllRounds(lines)
        }
}