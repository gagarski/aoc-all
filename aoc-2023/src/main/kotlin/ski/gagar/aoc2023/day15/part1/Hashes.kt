package ski.gagar.aoc2023.day15.part1

fun String.lavaHash(): Int {
    var hash = 0

    for (char in this) {
        hash += char.code
        hash *= 17
        hash %= 256
    }

    return hash
}

fun String.sumHashesCsv() =
    this.split(",").sumOf { it.lavaHash() }