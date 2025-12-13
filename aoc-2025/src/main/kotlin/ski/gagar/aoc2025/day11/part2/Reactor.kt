package ski.gagar.aoc2025.day11.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import ski.gagar.aoc2025.day11.part1.buildGraph
import ski.gagar.aoc2025.day11.part1.countPaths
import java.math.BigInteger

private fun <T> permutations(set: Set<T>): Sequence<List<T>> {
    fun <T> helper(list: PersistentList<T>): Sequence<PersistentList<T>> = sequence {
        if (list.isEmpty()) {
            yield(persistentListOf())
            return@sequence
        }

        for (i in list.indices) {
            yieldAll(helper(list.removeAt(i)).map {
                it.add(list[i])
            })
        }
    }

    return helper(set.toPersistentList())
}

fun countPathsIncluding(lines: Sequence<String>,
                        from: String = "svr", to: String = "out",
                        through: Set<String> = setOf("fft", "dac")): BigInteger {
    val g = buildGraph(lines)

    fun chunks(permutation: List<String>) = sequence {
        if (permutation.isEmpty()) {
            yield(from to to)
            return@sequence
        }

        var prev = from

        for (item in permutation) {
            yield(prev to item)
            prev = item
        }

        yield(prev to to)
    }

    return permutations(through).sumOf { permutation ->
        chunks(permutation).map {
            g.countPaths(it.first, it.second)
        }.reduce(BigInteger::times)
    }
}