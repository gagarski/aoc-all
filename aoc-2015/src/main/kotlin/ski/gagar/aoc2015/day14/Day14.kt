package ski.gagar.aoc2015.day14

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day14 : Puzzle {
    override val name: String = "Reindeer Olympics"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day14.part1.runDeers(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day14.part2.runDeers(lines)
        }
}