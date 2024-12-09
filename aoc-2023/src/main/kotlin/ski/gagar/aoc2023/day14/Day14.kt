package ski.gagar.aoc2023.day14

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day14.part1.loadAfterTilt
import ski.gagar.aoc2023.day14.part2.loadAfterSpin
import java.io.InputStream

object Day14 : Puzzle {
    override val name: String = "Parabolic Reflector Dish"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day14/rocks.txt"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            loadAfterTilt(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            loadAfterSpin(lines)
        }
}