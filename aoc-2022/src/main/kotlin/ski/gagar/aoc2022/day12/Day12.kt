package ski.gagar.aoc2022.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day12.part1.findShortestPath
import ski.gagar.aoc2022.day12.part2.findShortestScenicPath
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "Hill Climbing Algorithm"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            findShortestPath(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            findShortestScenicPath(lines)
        }
}