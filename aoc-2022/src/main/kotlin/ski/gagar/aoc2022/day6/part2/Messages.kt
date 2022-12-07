package ski.gagar.aoc2022.day6.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day6.part1.startOffset


fun day6Part2() {
    println("day6/part1/messages: ${
        startOffset(getResourceAsStream("/ski.gagar.aoc.aoc2022.day6/messages.txt").bufferedReader().lineSequence().first(), 14)
    }")
}
