package ski.gagar.aoc2025.day9

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day9.part1.biggestArea
import ski.gagar.aoc2025.day9.part2.floodfill.biggestAreaGreenMapped
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "Movie Theater"

    override fun part1(input: InputStream) = input.bufferedReader().useLines {
        biggestArea(it)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines {
        biggestAreaGreenMapped(it)
    }
}