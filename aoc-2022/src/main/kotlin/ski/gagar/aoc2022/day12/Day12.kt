package ski.gagar.aoc2022.day12

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day12.part1.findShortestPath
import ski.gagar.aoc2022.day12.part2.findShortestScenicPath
import java.io.InputStream

object Day12 : Puzzle {
    override val name: String = "Hill Climbing Algorithm"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day12/heights.txt"

    override fun part1(input: InputStream) = findShortestPath(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) = findShortestScenicPath(input.bufferedReader().lineSequence())
}