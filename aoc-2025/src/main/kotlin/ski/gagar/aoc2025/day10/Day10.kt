import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day10.part1.sumShortestPaths
import ski.gagar.aoc2025.day10.part2.sumShortestPathsForJoltages
import java.io.InputStream

object Day10 : Puzzle {
    override val name: String = "Factory"

    override fun part1(input: InputStream) = input.bufferedReader().lineSequence().let { sumShortestPaths(it) }
    override fun part2(input: InputStream) = input.bufferedReader().lineSequence().let { sumShortestPathsForJoltages(it) }
}