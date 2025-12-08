package ski.gagar.aoc2025.day4.part1

data class Coordinates(val row: Int, val column: Int) {
    val neighbors
        get() = sequence {
            yield(Coordinates(row - 1, column - 1))
            yield(Coordinates(row - 1, column))
            yield(Coordinates(row - 1, column + 1))
            yield(Coordinates(row, column - 1))
            yield(Coordinates(row, column + 1))
            yield(Coordinates(row + 1, column - 1))
            yield(Coordinates(row + 1, column))
            yield(Coordinates(row + 1, column + 1))
        }
}

class Field(lines: Sequence<String>) {
    val taken: Set<Coordinates>
    val height: Int
    val width: Int

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
        taken = tkn
        width = w
        height = h
    }

    private val Coordinates.isInside
        get() = this.row in 0..<height && this.column in 0..width

    private val Coordinates.neighborsInside
        get() = neighbors.filter { it.isInside }

    fun isAccessible(coordinates: Coordinates) =
        coordinates in taken && coordinates.neighborsInside.filter { it in taken }.count() < 4
}

fun Field.allCoordinates() = sequence {
    for (row in 0 until height) {
        for (column in 0 until width) {
            yield(Coordinates(row, column))
        }
    }
}

fun Field.countAccessible() =
    allCoordinates().count {
        isAccessible(it)
    }


