package ski.gagar.aoc2022.day3.part2

import ski.gagar.aoc2022.day2.part1.playAllRounds
import ski.gagar.aoc2022.day3.part1.priority
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

    val all: Set<Char>
        get() = first.union(second)

    companion object {
        fun parse(str: String): Rucksack {
            require(str.length % 2 == 0)
            return Rucksack(
                str.substring(0, str.length / 2).toSet(),
                str.substring(str.length / 2, str.length).toSet()
            )
        }

        fun badge(rucksacks: List<Rucksack>): Char {
            require(rucksacks.isNotEmpty())
            val intersection = rucksacks.first().all.toMutableSet()

            for (rucksack in rucksacks.asSequence().drop(1)) {
                intersection.retainAll(rucksack.all)
            }

            check(intersection.size == 1)
            return intersection.first()
        }
    }
}

fun sumPriorities(strings: Sequence<String>, chunkSize: Int = 3) =
    strings.chunked(chunkSize).sumOf { rucksack ->
        Rucksack.badge(rucksack.map { Rucksack.parse(it) }).priority
    }
