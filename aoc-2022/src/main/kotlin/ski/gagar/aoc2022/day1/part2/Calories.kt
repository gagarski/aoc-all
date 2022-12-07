package ski.gagar.aoc2022.day1.part2

import ski.gagar.aoc.util.getResourceAsStream
import java.util.TreeSet
import kotlin.math.max

fun calories(strings: Sequence<String>, n: Int = 3): Int {
    var current = 0
    val set = TreeSet<Int>(Comparator.reverseOrder())

    for (str in strings) {
        if (str.isBlank()) {
            set.add(current)
            current = 0
            continue
        }

        current += str.toInt()
    }

    set.add(current)

    return set.take(n).sum()
}

fun day1Part2() {
    println(
        "day1/part2/calories: ${
            calories(getResourceAsStream("/ski.gagar.aoc.aoc2022.day1/calories.txt").bufferedReader().lineSequence(), 3)
        }"
    )
}
