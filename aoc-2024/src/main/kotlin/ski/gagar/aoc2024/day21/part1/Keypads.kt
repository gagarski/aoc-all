package ski.gagar.aoc2024.day21.part1

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.abs

val NUMERIC_KEYPAD = listOf(
    listOf('7', '8', '9'),
    listOf('4', '5', '6'),
    listOf('1', '2', '3'),
    listOf(null, '0', 'A'),
)

val DIRECTIONAL_KEYPAD = listOf(
    listOf(null, '^', 'A'),
    listOf('<', 'v', '>'),
)


data class Coordinates(val row: Int, val col: Int)


class Keypad(private val buttons: List<List<Char?>>) {
    val coordinates: Map<Char, Coordinates>
    init {
        require(buttons.isNotEmpty())
        require(buttons.all { it.size == buttons.first().size })

        coordinates = buttons.withIndex().flatMap { (row, btns) ->
            btns.withIndex().mapNotNull { (col, btn) -> btn?.to(Coordinates(row, col)) }
        }.toMap()
    }

    fun paths(from: Char, to: Char) = sequence {
        val fromC = coordinates[from] ?: error("$from is not on the keypad")
        val toC = coordinates[to] ?: error("$to is not on the keypad")
        val rowDelta = toC.row - fromC.row
        val colDelta = toC.col - fromC.col

        val rowSeq = if (rowDelta > 0) "v" else "^"
        val colSeq = if (colDelta > 0) ">" else "<"

        val rcaPossible = buttons[toC.row][fromC.col] != null
        val craPossible = buttons[fromC.row][toC.col] != null

        if (rcaPossible) {
            yield(rowSeq.repeat(abs(rowDelta)) + colSeq.repeat(abs(colDelta)) + "A")
        }

        if (craPossible) {
            yield(colSeq.repeat(abs(colDelta)) + rowSeq.repeat(abs(rowDelta)) + "A")
        }
    }

    fun shortestSeq(input: String, nextKeypads: PersistentList<Keypad>, start: Char = 'A'): String {
        if (nextKeypads.isEmpty())
            return input
        var prev = start

        return buildString {
            for (char in input) {
                val toApp = paths(prev, char).toList().map {
                    nextKeypads[0].shortestSeq(it, nextKeypads.removeAt(0), start)
                }.minBy { it.length }

                append(toApp)
                prev = char
            }
        }
    }
}

fun typeSequence(sequence: String, nDirectional: Int = 3, start: Char = 'A'): String {
    require(nDirectional >= 1)
    val numeric = Keypad(NUMERIC_KEYPAD)
    val directional = (List(nDirectional) { Keypad(DIRECTIONAL_KEYPAD) }).toPersistentList()

    return numeric.shortestSeq(sequence, directional, start)
}

fun String.toKeypadNumber() = this.filter { it.isDigit() }.toInt()

fun sumComplexities(lines: Sequence<String>, nDirectional: Int = 3, start: Char = 'A') =
    lines.sumOf { it.toKeypadNumber() * typeSequence(it, nDirectional, start).length }