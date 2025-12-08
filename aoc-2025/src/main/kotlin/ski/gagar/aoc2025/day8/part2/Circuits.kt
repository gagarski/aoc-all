package ski.gagar.aoc2025.day8.part2

import ski.gagar.aoc.util.unionfind.UnionFind
import ski.gagar.aoc2025.day8.part1.Junction
import ski.gagar.aoc2025.day8.part1.distance3
import java.math.BigInteger
import java.util.PriorityQueue

fun UnionFind.isSingleSegment(): Boolean = count == 1

data class PossibleConnection(val first: Junction, val second: Junction) : Comparable<PossibleConnection> {
    override fun compareTo(other: PossibleConnection): Int =
        first.distanceToOther3(second).compareTo(other.first.distanceToOther3(other.second))

}

fun List<Junction>.connectTillSingle(): BigInteger {
    val ids = withIndex().associate { it.value to it.index }

    val possibleConnections =
        sequence {
            for (i in indices) {
                for (j in (i + 1)..<size) {
                    yield(PossibleConnection(this@connectTillSingle[i], this@connectTillSingle[j]))
                }
            }
        }.toList().let { list ->
            PriorityQueue(list)
        }

    val uf = UnionFind(size)
    while (possibleConnections.isNotEmpty()) {
        val conn = possibleConnections.poll()
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