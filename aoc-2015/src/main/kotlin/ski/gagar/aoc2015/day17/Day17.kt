package ski.gagar.aoc2015.day17

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day17 : Puzzle {
    override val name: String = "No Such Thing as Too Much"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day17.part1.nCombinations(lines)
        }


    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day17.part2.nCombinations(lines)
        }

}