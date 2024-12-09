package ski.gagar.aoc2024.day5.part1

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc.util.graphs.GraphBuilder

private fun buildPagesGraph(lines: Iterator<String>): Graph<Int> {
    val bld = GraphBuilder<Int>()

    for (line in lines) {
        if (line.isBlank())
            break
        val pages = line.split('|')
        require(pages.size == 2)
        val (first, second) = pages.map { it.toInt() }

        bld.addVertex(second)
        bld.addVertex(first)

        bld.addEdge(second, first)
    }

    return bld.build()
}

private fun String.parsePageSequence(): List<Int> = split(',').map(String::toInt)

private fun List<Int>.isValid(g: Graph<Int>): Boolean {
    val printed = mutableSetOf<Int>()

    for (page in this) {
        if (printed.any { g.getEdge(it, page) != null})
            return false
        printed.add(page)
    }

    return true
}

private fun List<Int>.middle(): Int {
    require(size % 2 == 1)
    return this[size / 2]
}

fun sumValidMiddlePages(lines: Sequence<String>): Int {
    val itr = lines.iterator()
    val g = buildPagesGraph(itr)

    return itr.asSequence()
        .map { it.parsePageSequence() }
        .filter { it.isValid(g) }
        .map { it.middle() }
        .sum()
}