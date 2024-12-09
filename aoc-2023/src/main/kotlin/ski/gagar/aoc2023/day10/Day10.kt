package ski.gagar.aoc2023.day10

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day10.part1.findFurthestFromAnimal
import ski.gagar.aoc2023.day10.part2.areaInsideMainLoop
import java.io.InputStream

object Day10 : Puzzle {
    override val name: String = "Pipe Maze"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day10/maze.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            findFurthestFromAnimal(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            areaInsideMainLoop(lines)
        }
}