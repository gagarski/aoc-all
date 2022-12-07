package ski.gagar.aoc2015.day10.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day9.part2.longestPath

fun lookAndSay(string: String): String {
    var last: Char? = null
    var repeatCtr = 1
    return buildString {
        for (char in string) {
            if (char != last) {
                if (last != null) {
                    append(repeatCtr)
                    append(last)
                }
                last = char
                repeatCtr = 1
            } else {
                repeatCtr++
            }
        }

        if (last != null) {
            append(repeatCtr)
            append(last)
        }
    }
}

fun lookAnsSayLength(string: String, iterations: Int = 40): Int {
    var current = string

    for (i in 0 until iterations) {
        current = lookAndSay(current)
    }

    return current.length
}

fun day10Part1() {
    println("day9/part2/look-and-say: ${
        lookAnsSayLength(getResourceAsStream("/ski.gagar.aoc.aoc2015.day10/look-and-say.txt").bufferedReader().lineSequence().first())
    }")
}
