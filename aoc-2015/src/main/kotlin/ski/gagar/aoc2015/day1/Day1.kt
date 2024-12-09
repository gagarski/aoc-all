package ski.gagar.aoc2015.day1

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day1 : Puzzle {
    override val name: String = "Not Quite Lisp"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day1/floors.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day1.part1.floors(lines.first())
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2015.day1.part2.floors(lines.first())
        }
}