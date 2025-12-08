package ski.gagar.aoc2025.day4.part2

import ski.gagar.aoc2025.day4.part1.Coordinates


class MutableField(lines: Sequence<String>) {
    private val taken_: MutableSet<Coordinates>
    val height: Int
    val width: Int

    val taken: Set<Coordinates>
        get() = taken_

    init {
        var h = 0
        var w = -1
        val tkn = mutableSetOf<Coordinates>()
        for (line in lines) {
            if (w == -1) {
                w = line.length
            } else if (w != line.length) {
                error("Field must be rectangular")
            }
            for (col in line.withIndex().filter { it.value == '@' }.map { it.index }) {
                tkn.add(Coordinates(h, col))
            }
            h++
        }
        taken_ = tkn
        width = w
        height = h
    }

    private val Coordinates.isInside
        get() = this.row in 0..<height && this.column in 0..width

    private val Coordinates.neighborsInside
        get() = neighbors.filter { it.isInside }

    fun isAccessible(coordinates: Coordinates) =
        coordinates in taken_ && coordinates.neighborsInside.filter { it in taken_ }.count() < 4

    fun remove(coordinates: Coordinates) =
        taken_.remove(coordinates)
}

fun MutableField.allCoordinates() = sequence {
    for (row in 0 until height) {
        for (column in 0 until width) {
            yield(Coordinates(row, column))
        }
    }
}

fun MutableField.allAccessible() =
    allCoordinates().filter {
        isAccessible(it)
    }


fun MutableField.cleanUp(): Int {
    var count = 0

    while (true) {
        val acc = allAccessible().toList()
        if (acc.isEmpty()) {
            break
        }
        count += acc.size
        for (c in acc) {
            remove(c)
        }
    }

    return count
}

