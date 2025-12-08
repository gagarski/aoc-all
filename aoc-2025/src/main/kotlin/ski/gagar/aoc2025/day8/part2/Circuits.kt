package ski.gagar.aoc2025.day8.part2

import ski.gagar.aoc.util.unionfind.UnionFind
import ski.gagar.aoc2025.day8.part1.Junction
import ski.gagar.aoc2025.day8.part1.distance3
import java.math.BigInteger

fun UnionFind.isSingleSegment(): Boolean = count == 1

fun List<Junction>.connectTillSingle(): BigInteger {
    val ids = withIndex().associate { it.value to it.index }
    val possibleConnections =
        asSequence()
            .flatMap { first ->
                asSequence()
                    .map {
                            second -> first to second
                    }
                    .filter { (first, second) -> first != second }
            }
            .sortedBy { it.distance3() }

    val uf = UnionFind(size)
    for (conn in possibleConnections) {
        uf.union(ids[conn.first]!!, ids[conn.second]!!)
        if (uf.isSingleSegment()) {
            return conn.first.x * conn.second.x
        }
    }
    return -BigInteger.ONE
}

fun connectTillSingle(lines: Sequence<String>): BigInteger =
    lines.map { Junction.parse(it) }
        .toList()
        .connectTillSingle()