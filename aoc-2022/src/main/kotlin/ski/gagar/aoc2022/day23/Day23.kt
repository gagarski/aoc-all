package ski.gagar.aoc2022.day23

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day23.part1.getNPlants
import ski.gagar.aoc2022.day23.part2.getNSteps
import java.io.InputStream

object Day23 : Puzzle {
    override val name: String = "Unstable Diffusion"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day23/plants.txt"

    override fun part1(input: InputStream) =
        getNPlants(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        getNSteps(input.bufferedReader().lineSequence())
}