package ski.gagar.aoc2015.day8.part2

import ski.gagar.aoc.util.getResourceAsStream

fun encode(string: String) = buildString {
    append('"')
    for (c in string) {
        append(
            when (c) {
                '\\' -> """\\"""
                '\"' -> """\""""
                else -> "$c"
            }
        )
    }
    append('"')
}

fun countDiff(strings: Sequence<String>) =
    strings.map { it to encode(it) }.sumOf { it.second.length - it.first.length }
