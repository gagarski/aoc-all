package ski.gagar.aoc2024.day21.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import ski.gagar.aoc2024.day21.part1.Coordinates
import ski.gagar.aoc2024.day21.part1.DIRECTIONAL_KEYPAD
import ski.gagar.aoc2024.day21.part1.Keypad
import ski.gagar.aoc2024.day21.part1.NUMERIC_KEYPAD
import kotlin.math.abs

private data class CacheKey(val from: Char, val to: Char, val keypadsRemaining: Int)

private fun Keypad.shortestSeqLengthCached(
    input: String,
    nextKeypads: PersistentList<Keypad>,
    start: Char = 'A',
    cache: MutableMap<CacheKey, Long> = mutableMapOf(),
): Long {
    if (nextKeypads.isEmpty())
        return input.length.toLong()
    var prev = start

    var res = 0L
    for (char in input) {
        val toAdd = cache[CacheKey(prev, char, nextKeypads.size)] ?: paths(prev, char).toList().minOf {
            nextKeypads[0].shortestSeqLengthCached(it, nextKeypads.removeAt(0), start, cache)
        }
        cache[CacheKey(prev, char, nextKeypads.size)] = toAdd
        res += toAdd
        prev = char
    }

    return res
}

fun typeSequenceLen(sequence: String, nDirectional: Int = 26, start: Char = 'A'): Long {
    require(nDirectional >= 1)
    val numeric = Keypad(NUMERIC_KEYPAD)
    val directional = (List(nDirectional) { Keypad(DIRECTIONAL_KEYPAD) }).toPersistentList()

    return numeric.shortestSeqLengthCached(sequence, directional, start)
}

fun String.toKeypadNumber() = this.filter { it.isDigit() }.toInt()

fun sumComplexities(lines: Sequence<String>, nDirectional: Int = 26, start: Char = 'A') =
    lines.sumOf { it.toKeypadNumber() * typeSequenceLen(it, nDirectional, start) }