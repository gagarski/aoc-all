package ski.gagar.aoc2024.day15

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day15.part1.sumBoxGps
import ski.gagar.aoc2024.day15.part2.sumBoxGpsWide
import java.io.InputStream

object Day15 : Puzzle {
    override val name: String = "Warehouse Woes"

    override fun part1(input: InputStream): Int = input.bufferedReader().useLines { lines ->
        sumBoxGps(lines)
    }

    override fun part2(input: InputStream): Int = input.bufferedReader().useLines { lines ->
        sumBoxGpsWide(lines)
    }
}