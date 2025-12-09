package ski.gagar.aoc2025.day9.part1

import ski.gagar.aoc.util.eachPairNoSwaps
import java.math.BigInteger

data class Seat(val row: BigInteger, val column: BigInteger) {
    fun areaAlongWithOther(other: Seat): BigInteger {
        return ((row - other.row).abs() + BigInteger.ONE) * ((column - other.column).abs() + BigInteger.ONE)
    }
}

fun parseSeat(string: String): Seat {
    val parts = string.split(",")
    require(parts.size == 2)
    return Seat(parts[1].toBigInteger(), parts[0].toBigInteger())
}

fun parseSeats(seq: Sequence<String>): List<Seat> =
    seq.map { parseSeat(it) }.toList()

fun biggestArea(seq: Sequence<String>): BigInteger {
    var biggestArea = BigInteger.ZERO
    val seats = parseSeats(seq)

    for (pair in seats.eachPairNoSwaps()) {
        val area = pair.first.areaAlongWithOther(pair.second)

        if (area > biggestArea)
            biggestArea = area
    }
    return biggestArea
}