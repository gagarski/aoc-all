package ski.gagar.aoc2025.day9.part2

import ski.gagar.aoc.util.eachPairNoSwaps
import ski.gagar.aoc2025.day9.part1.Seat
import ski.gagar.aoc2025.day9.part1.parseSeats
import java.math.BigInteger

val Pair<Seat, Seat>.isVerticalSide
    get() = first.column == second.column

val Pair<Seat, Seat>.isHorizontalSide
    get() = first.row == second.row

val Pair<Seat, Seat>.isSide
    get() = isVerticalSide || isHorizontalSide

@ConsistentCopyVisibility
data class Rectangle private constructor(val topLeft: Seat, val bottomRight: Seat) {
    fun contains(seat: Seat) =
        seat.row in topLeft.row..bottomRight.row && seat.column in topLeft.column..bottomRight.column

    fun containsStrict(seat: Seat) =
        seat.row in (topLeft.row + BigInteger.ONE)..(bottomRight.row - BigInteger.ONE)
                && seat.column in (topLeft.column + BigInteger.ONE)..(bottomRight.column - BigInteger.ONE)


    val area
        get() = (bottomRight.row - topLeft.row + BigInteger.ONE) * (bottomRight.column - topLeft.column + BigInteger.ONE)

    val topRight
        get() = Seat(topLeft.row, bottomRight.column)
    val bottomLeft
        get() = Seat(bottomRight.row, topLeft.column)

    fun borderSeats() = sequence {
        var row = topLeft.row
        var col = topLeft.column

        while (true) {
            yield(Seat(row, col))
            if (col == bottomRight.column) break
            col += BigInteger.ONE
        }

        while (true) {
            yield(Seat(row, col))
            if (row == bottomRight.row) break
            row += BigInteger.ONE
        }

        while (true) {
            col -= BigInteger.ONE
            if (col == topLeft.column) break
            yield(Seat(row, col))
        }
        while (true) {
            row -= BigInteger.ONE
            if (row == topLeft.row + BigInteger.ONE) break
            yield(Seat(row, col))
        }
    }

    companion object {
        operator fun invoke(first: Seat, second: Seat): Rectangle {
            val minRow = minOf(first.row, second.row)
            val maxRow = maxOf(first.row, second.row)
            val minColumn = minOf(first.column, second.column)
            val maxColumn = maxOf(first.column, second.column)
            return Rectangle(Seat(minRow, minColumn), Seat(maxRow, maxColumn))
        }
    }
}

fun Pair<Seat, Seat>.seats() = sequence {
    require(isSide)
    val minRow = minOf(first.row, second.row)
    val maxRow = maxOf(first.row, second.row)
    val minCol = minOf(first.column, second.column)
    val maxCol = maxOf(first.column, second.column)

    var row = minRow
    while (row <= maxRow) {
        var col = minCol
        while (col <= maxCol) {
            yield(Seat(row, col))
            col += BigInteger.ONE
        }
        row += BigInteger.ONE
    }

}

class MovieTheater(val redSeats: Set<Seat>) {
    val width = redSeats.maxOf { it.column } + BigInteger.ONE
    val height = redSeats.maxOf { it.row } + BigInteger.ONE

    val sides = redSeats
        .toList()
        .eachPairNoSwaps()
        .filter { it.isSide }
        .toSet()

    private val leftBorders: Map<BigInteger, BigInteger> = getLeftBorders()
    private val rightBorders: Map<BigInteger, BigInteger> = getRightBorders()
    private val topBorders: Map<BigInteger, BigInteger> = getTopBorders()
    private val bottomBorders: Map<BigInteger, BigInteger> = getBottomBorders()


    fun getLeftBorders(): Map<BigInteger, BigInteger> {
        val res = mutableMapOf<BigInteger, BigInteger>()
        for (side in sides.filter { it.isVerticalSide }) {
            for (seat in side.seats()) {
                val cur = res[seat.row]
                if (null == cur || seat.column < cur) {
                    res[seat.row] = seat.column
                }
            }
        }
        return res
    }

    fun getRightBorders(): Map<BigInteger, BigInteger> {
        val res = mutableMapOf<BigInteger, BigInteger>()
        for (side in sides.filter { it.isVerticalSide }) {
            for (seat in side.seats()) {
                val cur = res[seat.row]
                if (null == cur || seat.column > cur) {
                    res[seat.row] = seat.column
                }
            }
        }
        return res
    }

    fun getTopBorders(): Map<BigInteger, BigInteger> {
        val res = mutableMapOf<BigInteger, BigInteger>()
        for (side in sides.filter { it.isHorizontalSide }) {
            for (seat in side.seats()) {
                val cur = res[seat.column]
                if (null == cur || seat.row < cur) {
                    res[seat.column] = seat.row
                }
            }
        }
        return res
    }

    fun getBottomBorders(): Map<BigInteger, BigInteger> {
        val res = mutableMapOf<BigInteger, BigInteger>()
        for (side in sides.filter { it.isHorizontalSide }) {
            for (seat in side.seats()) {
                val cur = res[seat.column]
                if (null == cur || seat.row > cur) {
                    res[seat.column] = seat.row
                }
            }
        }
        return res
    }

    private fun Seat.isInside(): Boolean {
        if (row !in BigInteger.ZERO..<height) return false
        if (column !in BigInteger.ZERO..<width) return false

        val minCol = leftBorders[row]
        val maxCol = rightBorders[row]

        if (minCol == null || maxCol == null) return false
        if (column !in minCol..maxCol) return false

        val minRow = topBorders[column]
        val maxRow = bottomBorders[column]

        if (minRow == null || maxRow == null) return false
        if (row !in minRow..maxRow) return false

        return true
    }

    fun isGreenOrRed(seat: Seat): Boolean = seat.isInside()
    fun isRed(seat: Seat): Boolean = seat in redSeats
    fun isGreen(seat: Seat): Boolean = isGreenOrRed(seat) && !isRed(seat)

}

fun draw(lines: Sequence<String>) = parseSeats(lines).let {
    val theater = MovieTheater(it.toSet())
    val str = buildString {
        append("\n")
        for (row in 0..<theater.height.toInt()) {
            for (col in 0..<theater.width.toInt()) {
                val seat = Seat(row.toBigInteger(), col.toBigInteger())
                when {
                    theater.isRed(seat) -> append('#')
                    theater.isGreen(seat) -> append('X')
                    else -> append('.')
                }
            }
            append("\n")
        }
    }
    str
}

fun biggestAreaGreen(lines: Sequence<String>): BigInteger = parseSeats(lines).let {
    val mt = MovieTheater(it.toSet())
    var biggestArea = BigInteger.ZERO
    var i = 0
    for (pair in mt.redSeats.toList().eachPairNoSwaps()) {
        val rect = Rectangle(pair.first, pair.second)

        if (rect.area < biggestArea) {
            continue
        }

        if (rect.borderSeats().any { !mt.isGreenOrRed(it) }) {
            continue
        }
        println(rect)
        biggestArea = rect.area
    }
    return biggestArea
}
