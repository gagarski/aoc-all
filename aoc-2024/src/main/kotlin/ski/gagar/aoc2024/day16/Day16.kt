package ski.gagar.aoc2024.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day16.part1.labyrinthLowestScore
import ski.gagar.aoc2024.day16.part2.labyrinthBestSpots
import java.io.InputStream

object Day16 : Puzzle {
    override val name: String = "Reindeer Maze"

    override fun part1(input: InputStream): Int  = input.bufferedReader().useLines { lines ->
        labyrinthLowestScore(lines)
    }

    override fun part2(input: InputStream): Int  = input.bufferedReader().useLines { lines ->
        labyrinthBestSpots(lines)
    }
}