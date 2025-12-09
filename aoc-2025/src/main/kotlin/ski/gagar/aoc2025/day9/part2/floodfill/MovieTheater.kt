package ski.gagar.aoc2025.day9.part2.floodfill

import ski.gagar.aoc.util.eachPairNoSwaps
import ski.gagar.aoc2025.day9.part1.Seat
import ski.gagar.aoc2025.day9.part1.parseSeats
import ski.gagar.aoc2025.day9.part2.isSide
import java.math.BigInteger
import kotlin.minus
import kotlin.plus

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

    fun allSeats() = sequence {
        var row = topLeft.row
        while (row <= bottomRight.row) {
            var col = topLeft.column
            while (col <= bottomRight.column) {
                yield(Seat(row, col))
                col += BigInteger.ONE
            }
            row += BigInteger.ONE
        }
    }

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
            if (row <= topLeft.row + BigInteger.ONE) break
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

enum class Color {
    RED,
    GREEN,
    WHITE
}

class MovieTheater(redSeats: Set<Seat>) {
    private val colMapping: Map<BigInteger, BigInteger> =
        redSeats.asSequence().map { it.column }.sorted().toSet().asSequence().withIndex().associateBy({ it.value }, { it.index.toBigInteger() })
    private val colMappingRev: Map<BigInteger, BigInteger> = colMapping.asSequence().associateBy({it.value}, {it.key})
    private val rowMapping: Map<BigInteger, BigInteger> =
        redSeats.asSequence().map { it.row }.sorted().toSet().asSequence().withIndex().associateBy({ it.value }, { it.index.toBigInteger() })
    private val rowMappingRev: Map<BigInteger, BigInteger> = rowMapping.asSequence().associateBy({it.value}, {it.key})
    val width: BigInteger
    val height: BigInteger


    private val map: Map<Seat, Color>
    private val mappedRedSeats: List<Seat>

    private fun Seat.toMapped() =
        Seat(rowMapping[row]!!, colMapping[column]!!)
    private fun Seat.toUnmapped() = Seat(rowMappingRev[row]!!, colMappingRev[column]!!)

    init {
        mappedRedSeats = redSeats.map { it.toMapped() }
        width = mappedRedSeats.maxOf { it.column } + BigInteger.ONE
        height = mappedRedSeats.maxOf { it.row } + BigInteger.ONE
        var map = mappedRedSeats.associate { it to Color.RED }.toMutableMap()
        val sides = mappedRedSeats
            .toList()
            .eachPairNoSwaps()
            .filter { it.isSide }
            .toSet()

        for (side in sides) {
            val pseudoRect = Rectangle(side.first, side.second)

            for (border in pseudoRect.allSeats()) {
                if (map[border] != null) {
                    continue
                }
                map[border] = Color.GREEN
            }
        }

        val bigRect = Rectangle(
            Seat(BigInteger.ZERO, BigInteger.ZERO),
            Seat(height - BigInteger.ONE, width - BigInteger.ONE)
        )

        for (seat in bigRect.borderSeats()) {
            floodFill(seat, map, Color.WHITE, null)
        }

        for (seat in bigRect.allSeats()) {
            floodFill(seat, map, Color.GREEN, null)
        }
        this.map = map
    }

    fun Seat.isBorder() = row == BigInteger.ZERO || row == height - BigInteger.ONE ||
            column == BigInteger.ZERO || column == width - BigInteger.ONE

    fun Seat.isInside() = row in BigInteger.ZERO..<height && column in BigInteger.ZERO..<(width)

    fun Seat.neighbors() = sequence {
        val left = Seat(row, column - BigInteger.ONE)
        if (left.isInside())
            yield(left)
        val right = Seat(row, column + BigInteger.ONE)
        if (right.isInside())
            yield(right)
        val top = Seat(row - BigInteger.ONE, column)
        if (top.isInside())
            yield(top)
        val bottom = Seat(row + BigInteger.ONE, column)
        if (bottom.isInside())
            yield(bottom)

    }

    fun biggestFittingRectangle(): BigInteger {
        var biggestArea = BigInteger.ZERO
        rects@for ((first, second) in mappedRedSeats.eachPairNoSwaps()) {
            val rect = Rectangle(first, second)
            val unmappedRect = Rectangle(first.toUnmapped(), second.toUnmapped())
            val area = unmappedRect.area
            if (area < biggestArea)
                continue@rects
            for (seat in rect.allSeats()) {
                if (map[seat] != Color.GREEN && map[seat] != Color.RED) {
                    continue@rects
                }
            }
            biggestArea = area
        }
        return biggestArea
    }


    private fun floodFill(init: Seat, map: MutableMap<Seat, Color>, color: Color, patternColor: Color?) {
        val curColor = map[init]

        if (curColor != patternColor) {
            return
        }
        val queue = ArrayDeque<Seat>()
        queue.addLast(init)


        while (queue.isNotEmpty()) {
            val toFill = queue.removeFirst()
            val curColor = map[toFill]

            if (curColor != patternColor) {
                continue
            }

            map[toFill] = color

            for (n in toFill.neighbors()) {
                val nColor = map[n]
                if (nColor != patternColor) {
                    continue
                }
                queue.add(n)
            }
        }
    }

    override fun toString(): String = buildString {
        append("\n")
        for (row in 0..<height.toInt()) {
            for (col in 0..<width.toInt()) {
                val seat = Seat(row.toBigInteger(), col.toBigInteger())
                when {
                    map[seat] == Color.RED -> append('#')
                    map[seat] == Color.GREEN -> append('X')
                    map[seat] == Color.WHITE -> append('_')
                    else -> append('.')
                }
            }
            append("\n")
        }
    }
}

fun biggestAreaGreenMapped(lines: Sequence<String>): BigInteger = parseSeats(lines).let {
    val mt = MovieTheater(it.toSet())
    mt.biggestFittingRectangle()
}
