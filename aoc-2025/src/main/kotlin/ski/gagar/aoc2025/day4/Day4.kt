package ski.gagar.aoc2025.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2025.day4.part1.Field
import ski.gagar.aoc2025.day4.part1.countAccessible
import ski.gagar.aoc2025.day4.part2.MutableField
import ski.gagar.aoc2025.day4.part2.cleanUp
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "Printing Department"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines {
            Field(it).countAccessible()
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines {
            MutableField(it).cleanUp()
        }
}