package ski.gagar.aoc2022.day20

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day20 : Puzzle {
    override val name: String = "Grove Positioning System"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day20.part1.getResult(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day20.part2.getResult(lines)
        }
}