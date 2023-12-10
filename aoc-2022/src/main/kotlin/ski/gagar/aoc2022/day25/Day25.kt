package ski.gagar.aoc2022.day25

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day25.part1.genSnafuSum
import java.io.InputStream

object Day25 : Puzzle {
    override val name: String = "Full of Hot Air"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day25/snafu.txt"

    override fun part1(input: InputStream) =
        genSnafuSum(
            input.bufferedReader().lineSequence()
        )
}