package ski.gagar.aoc2022.day23.part2

import ski.gagar.aoc.util.getResourceAsStream

enum class Direction {
    N, S, W, E
}

data class Coordinates(val x: Int, val y: Int)

class ElfField(
    positions: Set<Coordinates>
) {
    var positions = positions
        private set

    var currentFirstDirection = Direction.N

    private fun directionsForStep() = sequence {
        val start = currentFirstDirection
        var current = start

       do {
            yield(current)
            current = nextDirection(current)
        } while (current != start)
    }

    private fun nextDirection(dir: Direction) = Direction.values()[(dir.ordinal + 1) % Direction.values().size]

    private fun roundRobinStartDirection() {
        currentFirstDirection = nextDirection(currentFirstDirection)
    }

    private fun neighbors(position: Coordinates) = sequence {
        val (x, y) = position

        yield(Coordinates(x, y - 1))
        yield(Coordinates(x + 1, y - 1))
        yield(Coordinates(x + 1, y))
        yield(Coordinates(x + 1, y + 1))
        yield(Coordinates(x, y + 1))
        yield(Coordinates(x - 1, y + 1))
        yield(Coordinates(x - 1, y))
        yield(Coordinates(x - 1, y - 1))
    }

    private fun neighborsOnDirection(position: Coordinates, direction: Direction) = sequence {
        val (x, y) = position
        when (direction) {
            Direction.N -> {
                yield(Coordinates(x - 1, y - 1))
                yield(Coordinates(x, y - 1))
                yield(Coordinates(x + 1, y - 1))
            }
            Direction.S -> {
                yield(Coordinates(x - 1, y + 1))
                yield(Coordinates(x, y + 1))
                yield(Coordinates(x + 1, y + 1))
            }
            Direction.W -> {
                yield(Coordinates(x - 1, y - 1))
                yield(Coordinates(x - 1, y))
                yield(Coordinates(x - 1, y + 1))
            }
            Direction.E -> {
                yield(Coordinates(x + 1, y - 1))
                yield(Coordinates(x + 1, y))
                yield(Coordinates(x + 1, y + 1))
            }
        }
    }



    private fun nextPositionForDirection(position: Coordinates, direction: Direction): Coordinates? {
        if (!neighborsOnDirection(position, direction).all { it !in positions })
            return null
        val (x, y) = position

        return when (direction) {
            Direction.N -> Coordinates(x, y - 1)
            Direction.S -> Coordinates(x, y + 1)
            Direction.W -> Coordinates(x - 1, y)
            Direction.E -> Coordinates(x + 1, y)
        }
    }

    private fun nextPositions(): Set<Coordinates> {
        val candidates = mutableMapOf<Coordinates, MutableList<Coordinates>>()
        for (position in positions) {

            val positionCandidate = when {
                neighbors(position).all { it !in positions } -> position
                else -> directionsForStep().firstNotNullOfOrNull { nextPositionForDirection(position, it) } ?: position
            }
            candidates[positionCandidate] = (candidates[positionCandidate] ?: mutableListOf()).apply {
                add(position)
            }
        }

        return sequence {
            for ((to, from) in candidates) {
                if (from.size != 1) {
                    for (f in from) {
                        yield(f)
                    }
                } else {
                    yield(to)
                }
            }
        }.toSet()
    }

    fun step(): Boolean {
        val old = positions
        positions = nextPositions()
        roundRobinStartDirection()

        return old == positions
    }

    fun steps(n: Int) {
        for (i in 0 until n) {
            step()
        }
    }

    fun stepWhileMoving(): Int {
        var i = 0

        while (true) {
            i++
            if (step()) {
                break
            }
        }

        return i
    }

    fun nFreeCells(): Int {
        val minX = positions.minOf { it.x }
        val maxX = positions.maxOf { it.x }
        val minY = positions.minOf { it.y }
        val maxY = positions.maxOf { it.y }

        return ((maxX - minX + 1) * (maxY - minY + 1)) - positions.size
    }

    override fun toString(): String {
        val minX = positions.minOf { it.x }
        val maxX = positions.maxOf { it.x }
        val minY = positions.minOf { it.y }
        val maxY = positions.maxOf { it.y }

        return buildString {
            for (y in minY .. maxY) {
                for (x in minX .. maxX) {
                    append(
                        when (Coordinates(x, y)) {
                            in positions -> "#"
                            else -> "."
                        }
                    )
                }
                append("\n")
            }
        }
    }
}

fun getNSteps(lines: Sequence<String>, nSteps: Int = 10): Int {
    val coords = sequence {
        for ((y, line) in lines.withIndex()) {
            for ((x, char) in line.withIndex()) {
                when (char) {
                    '#' -> yield(Coordinates(x, y))
                }
            }
        }
    }.toSet()

    val field = ElfField(coords)
    return field.stepWhileMoving()
}

fun day23Part2() {
    println("day23/part2/plants: \n${
        getNSteps(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day23/plants.txt").bufferedReader().lineSequence()
        )
    }")
}