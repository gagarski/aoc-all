package ski.gagar.aoc2022.day3.part1

import ski.gagar.aoc2022.day2.part1.playAllRounds
import ski.gagar.aoc.util.getResourceAsStream

val Char.priority
    get() = when (this) {
        in 'a'..'z' -> this - 'a' + 1
        in 'A'..'Z' -> this - 'A' + 27
        else -> throw IllegalArgumentException("Char $this has no priority")
    }

data class Rucksack(val first: Set<Char>, val second: Set<Char>) {
    val intersection: Set<Char>
        get() = first.intersect(second)

    val intersectionPriority
        get() = intersection.sumOf { it.priority }

    companion object {
        fun parse(str: String): Rucksack {
            require(str.length % 2 == 0)
            return Rucksack(
                str.substring(0, str.length / 2).toSet(),
                str.substring(str.length / 2, str.length).toSet()
            )
        }
    }
}

fun sumPriorities(strings: Sequence<String>) =
    strings.sumOf { Rucksack.parse(it).intersectionPriority }


fun day3Part1() {
    println("day3/part1/rucksacks: ${
        sumPriorities(getResourceAsStream("/ski.gagar.aoc.aoc2022.day3/rucksacks.txt").bufferedReader().lineSequence())
    }")
}
