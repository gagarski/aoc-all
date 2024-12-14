package ski.gagar.aoc2024.day13

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day13.part1.nTokens
import ski.gagar.aoc2024.day13.part2.nTokensOffset
import java.io.InputStream
import java.math.BigInteger

object Day13 : Puzzle {
    override val name: String = "Claw Contraption"
    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            nTokens(lines)
        }
    override fun part2(input: InputStream): BigInteger =
        input.bufferedReader().useLines { lines ->
            nTokensOffset(lines)
        }
}