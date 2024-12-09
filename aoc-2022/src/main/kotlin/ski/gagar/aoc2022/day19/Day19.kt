package ski.gagar.aoc2022.day19

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2022.day19.part1.RobotsParser
import java.io.InputStream
import ski.gagar.aoc2022.day19.part1.quality as qualityPart1
import ski.gagar.aoc2022.day19.part2.quality as qualityPart2

object Day19 : Puzzle {
    override val name: String = "Not Enough Minerals"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day19/robots.txt"

    override fun part1(input: InputStream) =
        RobotsParser.parse(
            input.bufferedReader().readTextAndClose()
        ).qualityPart1()

    override fun part2(input: InputStream) =
        RobotsParser.parse(
            input.bufferedReader().readTextAndClose()
        ).qualityPart2()
}