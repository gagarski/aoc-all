package ski.gagar.aoc2024.day22

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day22.part1.sumNthRandom
import ski.gagar.aoc2024.day22.part2.bestGain
import java.io.InputStream

object Day22 : Puzzle {
    override val name: String = "Monkey Market"

    override fun part1(input: InputStream) = input.bufferedReader().useLines { lines ->
        sumNthRandom(lines)
    }

    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        bestGain(lines)
    }
}