package ski.gagar.aoc2023.day2

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day2.part1.sumIdsIfIsPossible
import ski.gagar.aoc2023.day2.part2.sumPowers
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Cube Conundrum"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            sumIdsIfIsPossible(lines)
        }
    override fun part2(input: InputStream) = input.bufferedReader().useLines { lines ->
        sumPowers(lines)
    }
}