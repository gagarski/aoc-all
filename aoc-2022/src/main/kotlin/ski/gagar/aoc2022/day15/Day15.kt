package ski.gagar.aoc2022.day15

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2022.day15.part1.shadowSizeForRow
import ski.gagar.aoc2022.day15.part2.scan
import java.io.InputStream

object Day15 : Puzzle {
    override val name: String = "Beacon Exclusion Zone"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day15/beacons.txt"

    override fun part1(input: InputStream) = shadowSizeForRow(input.bufferedReader().readTextAndClose())
    override fun part2(input: InputStream) = scan(input.bufferedReader().readTextAndClose())
}