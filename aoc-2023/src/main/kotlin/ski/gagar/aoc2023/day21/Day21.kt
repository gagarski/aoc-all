package ski.gagar.aoc2023.day21

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day21.part1.countCells
import ski.gagar.aoc2023.day21.part2.countCellsInf
import java.io.InputStream

object Day21 : Puzzle {
    override val name: String = "Step Counter"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day21/garden.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countCells(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countCellsInf(lines)
        }
}