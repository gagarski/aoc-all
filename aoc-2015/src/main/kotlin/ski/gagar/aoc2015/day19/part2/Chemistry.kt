package ski.gagar.aoc2015.day19.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day19.part1.getAllReplacementsForOne
import ski.gagar.aoc2015.day19.part1.parseAllReplacements


fun String.getAllReplacements(replacements: List<Pair<String, String>>, buf: MutableSet<String>) {
    for ((from, to) in replacements) {
        getAllReplacementsForOne(from, to, buf)
    }

}

fun StringBuilder.replaceAll(from: String, to: String): Int {
    var index: Int = indexOf(from)
    var cnt = 0
    while (index != -1) {
        replace(index, index + from.length, to)
        cnt++
        index += to.length
        index = indexOf(from, index)
    }
    return cnt
}

/**
 * In general this "count-replacements" task is unsolvable for arbitrary inputs,
 * so here we take a greedy way and hope it works. It's not guaranteed to find the solution,
 * and it's not guaranteed to be best, but works for my input.
 *
 * See https://www.reddit.com/r/adventofcode/comments/3xflz8/day_19_solutions/#s
 */
fun countSteps(strings: Sequence<String>, init: String = "e"): Int {
    val iter = strings.iterator()

    val replacements = parseAllReplacements(iter).asSequence().map {
         it.second to it.first
    }.toList()

    require(iter.hasNext())
    val to = iter.next()

    val bld = StringBuilder(to)
    var steps = 0
    while (true) {
        var replacementsDone = 0
        for ((k, v) in replacements) {
            replacementsDone += bld.replaceAll(k, v)
        }

        if (replacementsDone == 0) {
            break
        }

        steps += replacementsDone

    }


    return if (bld.toString() == init) steps else -1
}