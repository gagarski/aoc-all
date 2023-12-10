package ski.gagar.aoc2023.day2

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day2.part1.sumIdsIfIsPossible
import ski.gagar.aoc2023.day2.part2.sumPowers
import java.io.InputStream

object Day2 : Puzzle {
    override val name: String = "Cube Conundrum"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day2/cubes.txt"

    override fun part1(input: InputStream) = sumIdsIfIsPossible(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = sumPowers(input.bufferedReader().lineSequence())
}