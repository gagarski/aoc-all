package ski.gagar.aoc2024.day12.part1

data class Coordinates(val row: Int, val column: Int) {
    fun neighbors() = sequence {
        yield(Coordinates(row - 1, column))
        yield(Coordinates(row, column + 1))
        yield(Coordinates(row + 1, column))
        yield(Coordinates(row, column - 1))
    }
}

data class Plant(val type: Char, val cells: Set<Coordinates>) {
    init {
        require(cells.isNotEmpty())
    }
    private val minRow = cells.minBy { it.row }.row
    private val maxRow = cells.maxBy { it.row }.row
    private val minColumn = cells.minBy { it.column }.column
    private val maxColumn = cells.maxBy { it.column }.column

    val height = (maxRow - minRow) + 1
    val width = (maxColumn - minColumn) + 1

    val area: Int
        get() = cells.size

    private fun hScan(): Int {
        val state = MutableList(width) { false }
        var perimeter = 0
        for (row in minRow..maxRow + 1) {
            for (column in minColumn..maxColumn) {
                val isInside = Coordinates(row, column) in cells
                val oldIsInside = state[column - minColumn]
                if (oldIsInside != isInside) {
                    perimeter++
                    state[column - minColumn] = isInside
                }
            }
        }
        return perimeter
    }

    private fun vScan(): Int {
        val state = MutableList(height) { false }
        var perimeter = 0
        for (column in minColumn..maxColumn + 1) {
            for (row in minRow..maxRow) {
                val isInside = Coordinates(row, column) in cells
                val oldIsInside = state[row - minRow]
                if (oldIsInside != isInside) {
                    perimeter++
                    state[row - minRow] = isInside
                }
            }
        }
        return perimeter
    }

    val perimeter: Int =
        hScan() + vScan()

    val fencePrice: Int =
        perimeter * area
}

class Field(lines: Sequence<String>) {
    val plants: Set<Plant>
    val height: Int
    val width: Int

    private fun List<String>.getPlantFrom(coordinates: Coordinates): Plant {
        val type = this[coordinates]

        val visited = mutableSetOf<Coordinates>()
        val queue = ArrayDeque<Coordinates>()
        queue.addLast(coordinates)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current in visited)
                continue

            for (n in current.neighbors().filter {
                it.isInside(this) && this[it] == type && it !in visited
            }) {
                queue.addLast(n)
            }
            visited.add(current)
        }

        return Plant(type, visited)
    }

    private operator fun List<String>.get(coordinates: Coordinates): Char =
        this[coordinates.row][coordinates.column]

    private fun Coordinates.isInside(field: List<String>): Boolean =
        this.row in field.indices && this.column in field[this.row].indices

    init {
        val field = lines.toList()

        require(field.isNotEmpty())
        require(field.all { it.length == field[0].length })
        height = field.size
        width = field[0].length

        val populated = mutableSetOf<Coordinates>()
        val plants = mutableSetOf<Plant>()
        for (row in 0 until height) {
            for (column in 0 until width) {
                val c = Coordinates(row, column)
                if (c in populated)
                    continue
                val plant = field.getPlantFrom(c)
                populated.add(c)
                plants.add(plant)
            }
        }

        this.plants = plants
    }
}

fun sumFencePrices(lines: Sequence<String>): Int = Field(lines).plants.sumOf {
    it.fencePrice
}