package ski.gagar.aoc2022.day18

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day18 : Puzzle {
    override val name: String = "Boiling Boulders"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day18.part1.processCubes(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day18.part2.processCubes(lines)
        }
}