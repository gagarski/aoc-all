package ski.gagar.aoc2022.day7.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day6.part1.startOffset
import ski.gagar.aoc2022.day7.part1.Console
import ski.gagar.aoc2022.day7.part1.Directory
import ski.gagar.aoc2022.day7.part1.Path
import java.io.FileNotFoundException
import java.util.StringJoiner


fun freeUp(sequence: Sequence<String>, total: Int = 70000000, minUnused: Int = 30000000): Int {
    val console = Console()
    console.consumeLines(sequence)
    val used = console.fs[Path()].size()
    return console.fs.lsR().asSequence()
        .map { it.fsItem }
        .filterIsInstance<Directory>()
        .sortedBy { it.size() }
        .first { total - used + it.size_ >= minUnused }
        .size()
}

fun day7Part2() {
    println("day7/part2/console: ${
        freeUp(getResourceAsStream("/ski.gagar.aoc.aoc2022.day7/console.txt").bufferedReader().lineSequence())
    }")
}
