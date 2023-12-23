package ski.gagar.aoc2023.day21.part1

data class Coordinates(val x: Int, val y: Int) {
    fun neighbors() = sequence {
        yield(Coordinates(x - 1, y))
        yield(Coordinates(x + 1, y))
        yield(Coordinates(x, y - 1))
        yield(Coordinates(x, y + 1))
    }
}

class Garden(val rocks: Set<Coordinates>, val width: Int, val height: Int) {
    operator fun contains(coordinates: Coordinates) =
        coordinates.x in 0 until width && coordinates.y in 0 until height

    private fun canStep(coordinates: Coordinates) = contains(coordinates) && coordinates !in rocks

    fun coveredCoords(from: Coordinates, steps: Int): Set<Coordinates> {
        val visited = mutableSetOf<Coordinates>()
        val queue = ArrayDeque<QueueItem>()
        queue.add(QueueItem(from))
        var currentLayer = 0
        while (queue.isNotEmpty()) {
            val (xy, layer) = queue.removeFirst()
            if (layer > steps) break
            if (layer > currentLayer) visited.clear()
            currentLayer = layer
            if (xy in visited) continue
            visited.add(xy)

            for (n in xy.neighbors()) {
                if (canStep(n)) queue.add(QueueItem(n, layer + 1))
            }
        }

        return visited
    }

    private data class QueueItem(val coordinates: Coordinates, val layer: Int = 0)
    data class Parsed(val garden: Garden, val start: Coordinates)

    companion object {
        fun from(lines: Sequence<String>): Parsed {
            var w: Int? = null
            var h = 0
            val rocks = mutableSetOf<Coordinates>()
            var start: Coordinates? = null
            for ((y, line) in lines.withIndex()) {
                require(w == null || line.length == w)
                w = line.length
                for ((x, char) in  line.withIndex()) {
                    when (char) {
                        '#' -> rocks.add(Coordinates(x, y))
                        'S' -> {
                            require(start == null)
                            start = Coordinates(x, y)
                        }
                    }
                }
                h++
            }

            require(h != 0)
            check(w != null)
            require(start != null)
            return Parsed(Garden(rocks, w, h), start)
        }
    }
}

fun countCells(lines: Sequence<String>, steps: Int = 64): Int {
    val (garden, start) = Garden.from(lines)
    return garden.coveredCoords(start, steps).size
}