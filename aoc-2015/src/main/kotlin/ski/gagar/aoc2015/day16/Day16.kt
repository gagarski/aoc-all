package ski.gagar.aoc2015.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.getResourceAsStream
import java.io.InputStream

object Day16 : Puzzle {
    override val name: String = "Aunt Sue"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day16/descriptions.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day16.part1.findFirstMatching(
                lines
            )
        }


    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day16.part2.findFirstMatching(
                lines
            )
        }
}