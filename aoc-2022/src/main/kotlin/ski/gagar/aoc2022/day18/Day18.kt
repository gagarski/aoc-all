package ski.gagar.aoc2022.day18

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day18 : Puzzle {
    override val name: String = "Boiling Boulders"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day18/cubes.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day18.part1.processCubes(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day18.part2.processCubes(input.bufferedReader().lineSequence())
}