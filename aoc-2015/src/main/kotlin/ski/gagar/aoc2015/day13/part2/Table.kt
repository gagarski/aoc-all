package ski.gagar.aoc2015.day13.part2

import ski.gagar.aoc.util.StringGraph
import ski.gagar.aoc.util.StringGraphBuilder
import ski.gagar.aoc2015.day13.part1.TableParser
import ski.gagar.aoc2015.day13.part1.findLongestPath
import ski.gagar.aoc2015.day13.part1.pathLength

fun sittingGraphWithMe(strings: Sequence<String>): StringGraph {
    val builder = StringGraphBuilder()
    for (string in strings) {
        val cond = TableParser.parse(string)
        val currentWeightDirect = builder.getEdge(cond.first, cond.second)?.weight ?: 0
        builder.addVertex(cond.first)
        builder.addVertex(cond.second)
        builder.addEdge(cond.first, cond.second, currentWeightDirect + cond.weight)
        val currentWeightReverse = builder.getEdge(cond.second, cond.first)?.weight ?: 0
        builder.addEdge(cond.second, cond.first, currentWeightReverse + cond.weight)
    }
    val me = "Me"
    builder.addVertex(me)

    for (vertex in builder.vertices) {
        builder.addEdge(me, vertex, 0)
        builder.addEdge(vertex, me, 0)
    }

    return builder.build()
}

fun optimalHappinessWithMe(strings: Sequence<String>) = sittingGraphWithMe(strings).findLongestPath()?.pathLength
