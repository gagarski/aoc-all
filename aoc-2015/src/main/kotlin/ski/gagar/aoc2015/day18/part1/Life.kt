package ski.gagar.aoc2015.day18.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.util.*

fun defaultNextState(state: CellWithNeighbors): Boolean {
    val neighbours = state.neigbours
    val me = state.me
    return when {
        me && neighbours.count { it } !in 2..3 -> false
        !me && neighbours.count { it } == 3 -> true
        else -> me
    }
}

data class CellWithNeighbors(
    val me: Boolean,
    val lu: Boolean,
    val u: Boolean,
    val ru: Boolean,
    val r: Boolean,
    val rb: Boolean,
    val b: Boolean,
    val lb: Boolean,
    val l: Boolean
) {
    val neigbours
        get() = listOf(lu, u, ru, r, rb, b, lb, l)
}

class Life(
    cells: BitSet,
    val width: Int,
    val height: Int,
    val nextState: (CellWithNeighbors) -> Boolean = ::defaultNextState
) {

    private var primary = BitSet(cells.size()).apply{
        for (i in 0 until cells.size()) {
            set(i, cells.get(i))
        }
    }
    private var secondary = BitSet(cells.size())

    private fun checkBounds(x: Int, y: Int) {
        check(x in 0 until width)
        check(y in 0 until height)
    }

    private fun index(x: Int, y: Int) = y * width + x

    private fun getOrFalse(x: Int, y: Int): Boolean {
        if (x !in 0 until width) return false
        if (y !in 0 until height) return false

        return primary.get(index(x, y))
    }

    operator fun get(x: Int, y: Int): Boolean {
        checkBounds(x, y)
        return primary.get(index(x, y))
    }

    private fun stateAt(x: Int, y: Int): CellWithNeighbors {
        checkBounds(x, y)
        return CellWithNeighbors(
            me = get(x, y),
            lu = getOrFalse(x - 1, y - 1),
            u = getOrFalse(x, y - 1),
            ru = getOrFalse(x + 1, y - 1),
            r = getOrFalse(x + 1, y),
            rb = getOrFalse(x + 1, y + 1),
            b = getOrFalse(x, y + 1),
            lb = getOrFalse(x - 1, y + 1),
            l = getOrFalse(x - 1, y)
        )
    }

    fun step() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                secondary.set(index(x, y), nextState(stateAt(x, y)))
            }
        }
        val tmp = primary
        primary = secondary
        secondary = tmp
    }

    fun run(n: Int = 1) {
        for (i in 0 until n) {
            step()
        }
    }

    val cardinality
        get() = primary.cardinality()

    override fun toString(): String = buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                append(if (get(x, y)) '#' else '.')
            }
            append('\n')
        }
    }
}

fun BitSet.append(string: String, offset: Int = 0) {
    for ((i, c) in string.withIndex()) {
        set(i + offset, c == '#')
    }
}

fun parseLife(strings: Sequence<String>, nextState: (CellWithNeighbors) -> Boolean = ::defaultNextState): Life {
    val bitSet = BitSet()
    val itr = strings.iterator()

    require(itr.hasNext())

    val first = itr.next()
    val width = first.length
    var height = 1

    var offset = 0

    bitSet.append(first)

    for (line in itr) {
        require(line.length == width)
        offset += width
        bitSet.append(line, offset)
        height++
    }

    return Life(bitSet, width, height, nextState)
}

fun parseAndRun(strings: Sequence<String>, iterations: Int = 100, nextState: (CellWithNeighbors) -> Boolean = ::defaultNextState) =
    parseLife(strings, nextState).apply { run(iterations) }.cardinality
