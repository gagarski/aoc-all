package ski.gagar.aoc2024.day18.part2

import ski.gagar.aoc2024.day18.part1.Memory

fun cutOffAddress(lines: Sequence<String>, width: Int = 71, height: Int = 71): String {
    val corruptedLines = lines.toList()

    for (n in 0 .. corruptedLines.size) {
        val mem = Memory(corruptedLines.asSequence(), n, width, height)
        val sp = mem.shortestPath()
        when {
            sp == null && n == 0 -> error("Memory is already corrupted at the beginning")
            sp == null -> return corruptedLines[n - 1]
        }
    }
    error("Memory is never corrupted")
}