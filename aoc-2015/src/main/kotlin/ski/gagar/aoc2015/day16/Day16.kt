package ski.gagar.aoc2015.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.getResourceAsStream
import java.io.InputStream

private const val UNKNOWN_INPUT_PATH = "/ski.gagar.aoc.aoc2015.day16/unknown.txt"

object Day16 : Puzzle {
    override val name: String = "Aunt Sue"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day16/descriptions.txt"

    override fun part1(input: InputStream) =
        getResourceAsStream(UNKNOWN_INPUT_PATH).use { unknown ->
            ski.gagar.aoc2015.day16.part1.findFirstMatching(
                unknown.bufferedReader().lineSequence(),
                input.bufferedReader().lineSequence()
            )
        }

    override fun part2(input: InputStream) =
        getResourceAsStream(UNKNOWN_INPUT_PATH).use { unknown ->
            ski.gagar.aoc2015.day16.part2.findFirstMatching(
                unknown.bufferedReader().lineSequence(),
                input.bufferedReader().lineSequence()
            )
        }
}