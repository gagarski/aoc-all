package ski.gagar.aoc2022.day16

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day16.part1.VolcanoParser
import java.io.InputStream

object Day16 : Puzzle {
    override val name: String = "Proboscidea Volcanium"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day16/volcano.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day16.part1.bestCourseOfAction(
            VolcanoParser.parse(
                input.bufferedReader().readText()
            )
        )?.result

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day16.part2.bestCourseOfAction(
            VolcanoParser.parse(
                input.bufferedReader().readText()
            )
        )?.result
}