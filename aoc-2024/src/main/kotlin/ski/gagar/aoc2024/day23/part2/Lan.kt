package ski.gagar.aoc2024.day23.part2

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc2024.day23.part1.buildLan

fun <V> Graph<V>.neighbors(v: V): Set<V> = getEdgesFrom(v).keys

fun <V> Graph<V>.bronKerbosch(r: Set<V>, p: Set<V>, x: Set<V>): Sequence<Set<V>> = sequence {
    if (p.isEmpty() && x.isEmpty())
        yield(r)

    val pivot = (p - x).maxByOrNull { neighbors(it).size }
    val next = if (pivot != null) p - (neighbors(pivot)) else p

    val mp = p.toMutableSet()
    val mx = x.toMutableSet()


    for (v in next) {
        yieldAll(bronKerbosch(r + v, p intersect neighbors(v), x intersect neighbors(v)))
        mp.remove(v)
        mx.add(v)
    }
}

fun <V> Graph<V>.maximalCliques() = bronKerbosch(emptySet(), vertices, emptySet())

fun <V> Graph<V>.maximumClique() = bronKerbosch(emptySet(), vertices, emptySet()).maxBy { it.size }

fun maximumClique(lines: Sequence<String>): String =
    buildLan(lines).maximumClique().sorted().joinToString(",")
