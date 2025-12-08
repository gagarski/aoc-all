package ski.gagar.aoc2025.day7.part1

import ski.gagar.aoc2025.day4.part1.Coordinates

data class Coordinates(val row: Int, val col: Int)

class Field(lines: Sequence<String>) {
    val start: Coordinates
    val splitters: Set<Coordinates>
    val height: Int
    val width: Int

    init {
        var h = 0
        var w = -1
        val splitters = mutableSetOf<Coordinates>()
        var start: Coordinates? = null
        for (line in lines) {
            if (w == -1) {
                w = line.length
            } else if (w != line.length) {
                error("Field must be rectangular")
            }
            for ((col, char) in line.withIndex()) {
                when (char) {
                    'S' -> {
                        check(start == null)
                        start = Coordinates(h, col)
                    }
                    '^' -> splitters.add(Coordinates(h, col))
                    '.' -> {}
                    else -> error("Unexpected char: $char")
                }
            }
            h++
        }
        require(start != null)
        this.start = start
        this.splitters = splitters
        this.width = w
        this.height = h
    }

    val Coordinates.isInside
        get() = this.row in 0..<height && this.column in 0..width

    fun countSplits(): Int {
        var beams = setOf(start)
        var row = start.row
        var splits = 0

        while (row < height - 1) {
            beams = sequence {
                for (newBeam in beams.asSequence().map { Coordinates(it.row + 1, it.column) }) {
                    if (newBeam in splitters) {
                        val left = Coordinates(newBeam.row, newBeam.column - 1)
                        val right = Coordinates(newBeam.row, newBeam.column + 1)
                        if (left.isInside) {
                            yield(left)
                        }
                        if (right.isInside) {
                            yield(right)
                        }
                        splits++
                    } else {
                        yield(newBeam)
                    }
                }
            }.toSet()
            row++
        }
        return splits
    }
}