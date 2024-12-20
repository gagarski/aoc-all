package ski.gagar.aoc2024.day20.part1

data class Coordinates(val row: Int, val col: Int) {
    fun neighbors() = sequence {
        yield(Coordinates(row - 1, col))
        yield(Coordinates(row, col + 1))
        yield(Coordinates(row + 1, col))
        yield(Coordinates(row, col - 1))
    }

    fun pairsOfNeighbors() = sequence {
        for (n1 in neighbors()) {
            for (n2 in neighbors()) {
                if (n1 != n2)
                    yield(n1 to n2)
            }
        }
    }
}

class Path(val steps: List<Coordinates>) {
    private val indices = steps.withIndex().associate { it.value to it.index }
    private val stepsSet = steps.toSet()

    fun hasStep(step: Coordinates) = step in stepsSet
    fun distance(from: Coordinates, to: Coordinates): Int {
        require(from in stepsSet && to in stepsSet)
        return indices[to]!! - indices[from]!!
    }

    override fun toString(): String = "Path($steps)"
}

data class Cheat(val from: Coordinates, val to: Coordinates, val savedTime: Int)

class Racetrack(lines: Sequence<String>) {
    val width: Int
    val height: Int
    val walls: Set<Coordinates>
    val start: Coordinates
    val end: Coordinates

    init {
        var w: Int? = null
        var h = 0
        val walls = mutableSetOf<Coordinates>()
        var start: Coordinates? = null
        var end: Coordinates? = null

        for ((row, line) in lines.withIndex()) {
            if (null != w)
                require(line.length == w)

            h++
            w = line.length

            for ((col, char) in line.withIndex()) {
                when (char) {
                    '.' -> {}
                    '#' ->
                        walls.add(Coordinates(row, col))
                    'S' -> {
                        require(start == null)
                        start = Coordinates(row, col)
                    }
                    'E' -> {
                        require(end == null)
                        end = Coordinates(row, col)
                    }
                }
            }

        }

        require(h != 0)
        check(w != null)
        require(start != null)
        require(end != null)
        width = w
        height = h
        this.walls = walls
        this.start = start
        this.end = end
    }

    operator fun contains(coordinates: Coordinates): Boolean =
        coordinates.row in 0 ..< height && coordinates.col in 0 ..< width

    fun findSinglePath(): Path {
        val path = mutableListOf<Coordinates>()
        var current = start
        var prev: Coordinates? = null

        while (current != end) {
            path.add(current)
            val neighbors = current.neighbors().filter {
                it in this && it !in walls && (prev == null || it != prev)
            }.toList()
            check(neighbors.size == 1)
            prev = current
            current = neighbors.first()
        }

        path.add(end)

        return Path(path)
    }

    fun cheats(path: Path = findSinglePath()): Sequence<Cheat> = sequence {
        for (wall in walls) {
            for (pair in wall.pairsOfNeighbors().filter {
                path.hasStep(it.first) && path.hasStep(it.second)
            }) {
                val origDistance = path.distance(pair.first, pair.second)

                if (origDistance > 2) {
                    yield(Cheat(pair.first, pair.second, origDistance - 2))
                }
            }
        }
    }
}

fun countCheatsLongerThan(lines: Sequence<String>, minCount: Int = 100): Int {
    val track = Racetrack(lines)
    return track.cheats().filter { it.savedTime >= minCount }.count()
}