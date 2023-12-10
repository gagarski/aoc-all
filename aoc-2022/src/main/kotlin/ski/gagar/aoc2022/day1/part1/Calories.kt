package ski.gagar.aoc2022.day1.part1

import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.max


fun calories(strings: Sequence<String>): Int {
    var max = 0
    var current = 0

    for (str in strings) {
        if (str.isBlank()) {
            max = max(max, current)
            current = 0
            continue
        }

        current += str.toInt()
    }

    max = max(max, current)

    return max
}