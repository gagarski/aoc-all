package ski.gagar.aoc2022.day25

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day25.part1.genSnafuSum
import java.io.InputStream

object Day25 : Puzzle {
    override val name: String = "Full of Hot Air"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            genSnafuSum(
                lines
            )
        }

}