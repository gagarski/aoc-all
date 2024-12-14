package ski.gagar.aoc2024.day14

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2024.day14.part1.mulQuadrants
import ski.gagar.aoc2024.day14.part2.findTreeCandidate
import java.io.InputStream

object Day14 : Puzzle {
    override val name: String = "Restroom Redoubt"
    override fun part1(input: InputStream) = mulQuadrants(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = findTreeCandidate(input.bufferedReader().readTextAndClose())
}