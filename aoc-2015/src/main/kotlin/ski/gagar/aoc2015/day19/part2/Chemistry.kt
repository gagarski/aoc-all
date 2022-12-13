package ski.gagar.aoc2015.day19.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day19.part1.getAllReplacementsForOne
import ski.gagar.aoc2015.day19.part1.parseAllReplacements

fun String.getAllReplacements(replacements: List<Pair<String, String>>, buf: MutableSet<String>) {
    for ((from, to) in replacements) {
        getAllReplacementsForOne(from, to, buf)
    }

}

fun countSteps(strings: Sequence<String>, init: String = "e"): Int {
    val iter = strings.iterator()

    val replacements = parseAllReplacements(iter).asSequence().map {
         it.second to it.first
    }.toList()

    require(iter.hasNext())
    val to = iter.next()

    var res = setOf(to)
    var from: List<String>
    var cnt = 0

    while (init !in res) {
        cnt++
        from = res.sortedBy { it.length }
        res = mutableSetOf()
        for (f in from) {
            f.getAllReplacements(replacements, res)
        }
    }

    return cnt
}

fun day19Part2() {
    println("day19/part2/chemistry: ${
        countSteps(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day19/chemistry.txt").bufferedReader().lineSequence()
        )
    }")
}
