package ski.gagar.aoc2023.day22

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day22.part1.nDisintegratable
import ski.gagar.aoc2023.day22.part2.sumFallen
import java.io.InputStream

object Day22 : Puzzle {
    override val name: String = "A Long Walk"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day22/bricks.txt"
    override fun part1(input: InputStream) = nDisintegratable(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream) = sumFallen(input.bufferedReader().lineSequence())
}