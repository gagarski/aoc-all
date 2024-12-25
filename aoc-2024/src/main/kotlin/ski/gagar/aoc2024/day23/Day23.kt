package ski.gagar.aoc2024.day23

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day23.part1.countInterconnectedThrees
import ski.gagar.aoc2024.day23.part2.maximumClique
import java.io.InputStream

object Day23 : Puzzle {
    override val name: String = "LAN Party"
    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        countInterconnectedThrees(lines)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        maximumClique(lines)
    }
}