package ski.gagar.aoc2015.day10.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day10.part1.lookAnsSayLength

// Code from part1 fully fits!

fun day10Part2() {
    println("day10/part2/look-and-say: ${
        lookAnsSayLength(getResourceAsStream("/ski.gagar.aoc.aoc2015.day10/look-and-say.txt").bufferedReader().lineSequence().first(), 50)
    }")
}
