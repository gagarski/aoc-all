package ski.gagar.aoc2023.day17

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day17 : Puzzle {
    override val name: String = "Clumsy Crucible"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day17/buildings.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2023.day17.part1.minHeatLoss(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2023.day17.part2.minHeatLoss(input.bufferedReader().lineSequence())
}