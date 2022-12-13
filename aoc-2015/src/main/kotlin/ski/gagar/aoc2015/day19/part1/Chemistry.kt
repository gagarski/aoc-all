package ski.gagar.aoc2015.day19.part1

import ski.gagar.aoc.util.getResourceAsStream

fun String.getAllReplacementsForOne(from: String, to: String, buf: MutableSet<String>) {
    var indexFrom = 0

    do {
        val indexOf = indexOf(from, indexFrom)
        if (-1 == indexOf) {
            return
        }
        buf.add(replaceRange(indexOf until (indexOf + from.length), to))

        indexFrom = indexOf + 1
    } while (true)
}

fun String.getAllReplacements(replacements: List<Pair<String, String>>): Set<String> {
    val res = mutableSetOf<String>()

    for ((from, to) in replacements) {
        getAllReplacementsForOne(from, to, res)
    }

    return res
}


private val REPLACEMENT_RE = """(.*?)\s+=>\s+(.*)""".toRegex()

fun String.toReplacement(): Pair<String, String> {
    val match = REPLACEMENT_RE.matchEntire(this)
    require(match != null)
    return match.groups[1]!!.value to match.groups[2]!!.value
}

fun parseAllReplacements(stringIter: Iterator<String>): List<Pair<String, String>> {
    val res = mutableListOf<Pair<String, String>>()
    while (stringIter.hasNext()) {
        val next = stringIter.next()
        if (next.isBlank())
            return res
        res += next.toReplacement()
    }

    return res
}

fun countReplacement(strings: Sequence<String>): Int {
    val iter = strings.iterator()

    val replacements = parseAllReplacements(iter)

    require(iter.hasNext())
    val from = iter.next()

    return from.getAllReplacements(replacements).size
}

fun day19Part1() {
    println("day19/part1/chemistry: ${
        countReplacement(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day19/chemistry.txt").bufferedReader().lineSequence()
        )
    }")
}
