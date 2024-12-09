package ski.gagar.aoc2015.day7

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day7.part1.makeCircuitAndEvaluate
import ski.gagar.aoc2015.day7.part2.makeCircuitAndEvaluateThenReassign
import java.io.InputStream

object Day7 : Puzzle {
    override val name: String = "Some Assembly Required"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day7/circuits.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            makeCircuitAndEvaluate(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            makeCircuitAndEvaluateThenReassign(lines)
        }
}