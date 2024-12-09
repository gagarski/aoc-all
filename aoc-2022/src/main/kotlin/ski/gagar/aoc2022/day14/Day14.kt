package ski.gagar.aoc2022.day14

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day14 : Puzzle {
    override val name: String = "Regolith Reservoir"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day14/cave.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day14.part1.simulate(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            ski.gagar.aoc2022.day14.part2.simulate(lines)
        }
}