package ski.gagar.aoc2023.day23

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day23.part1.trailsLongestPath
import ski.gagar.aoc2023.day23.part2.trailsLongestPathNoSlopes
import java.io.InputStream

object Day23 : Puzzle {
    override val name: String = "A Long Walk"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            trailsLongestPath(lines)
        }
    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            trailsLongestPathNoSlopes(lines)
        }
}