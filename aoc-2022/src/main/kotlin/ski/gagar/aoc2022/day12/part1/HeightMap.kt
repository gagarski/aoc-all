package ski.gagar.aoc2022.day12.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day10.part1.countStrength
import java.util.PriorityQueue
import kotlin.math.absoluteValue

data class Point(val height: Char) {
    init {
        require(height == 'S' || height == 'E' || height in 'a'..'z')
    }

    fun canGoTo(other: Point): Boolean {
        if (this.height == 'S') return other.height == 'a'
        if (other.height == 'E') return this.height == 'z'

        return (other.height - this.height) <= 1
    }
}

data class Coordinates(val x: Int, val y: Int)

data class PointWithCoordinates(val coordinates: Coordinates, val point: Point)

data class Path(val path: List<PointWithCoordinates>, val weight: Int)

class HeightMap(private val map: List<List<Point>>) {
    val height = map.size
    val width: Int

    init {
        require(map.isNotEmpty())
        val widths = map.asSequence().map { it.size }.toSet()
        require(widths.size == 1)
        width = widths.first()
    }

    fun checkBounds(coordinates: Coordinates) {
        val (x, y) = coordinates
        require(x in 0 until width)
        require(y in 0 until height)
    }

    operator fun get(coordinates: Coordinates): Point {
        checkBounds(coordinates)
        return map[coordinates.y][coordinates.x]
    }

    private fun getNeighbors(coordinates: Coordinates) = sequence {
        checkBounds(coordinates)
        val (x, y) = coordinates
        if (x != 0) yield(PointWithCoordinates(Coordinates(x - 1, y), map[y][x -1]))
        if (x != width - 1) yield(PointWithCoordinates(Coordinates(x + 1, y), map[y][x + 1]))
        if (y != 0) yield(PointWithCoordinates(Coordinates(x, y - 1), map[y - 1][x]))
        if (y != height - 1) yield(PointWithCoordinates(Coordinates(x, y + 1), map[y + 1][x]))
    }

    private operator fun <T> MutableList<MutableList<T>>.get(coordinates: Coordinates) =
        this[coordinates.y][coordinates.x]

    private operator fun <T> MutableList<MutableList<T>>.set(coordinates: Coordinates, value: T) {
        this[coordinates.y][coordinates.x] = value
    }

    fun shortestPath(from: Coordinates, to: Coordinates): Path {
        val distances: MutableList<MutableList<Int?>> = MutableList(height) { MutableList(width) { null } }
        val paths : MutableList<MutableList<PointWithCoordinates?>> = MutableList(height) { MutableList(width) { null } }
        val visited: MutableSet<Coordinates> = mutableSetOf()

        distances[from] = 0

        val queue = PriorityQueue<Pair<Coordinates, Int>>(Comparator.comparing { it.second })

        queue.add(from to distances[from]!!)

        while (!queue.isEmpty()) {
            val (current, weight) = queue.remove()
            if (current in visited) {
                continue
            }

            visited.add(current)

            for (neighbor in getNeighbors(current)) {
                val nextPoint = get(current)

                if (!nextPoint.canGoTo(neighbor.point)) {
                    continue
                }

                val currentDistNeighbor = distances[neighbor.coordinates]
                val newDist = distances[current]!! + 1

                if (currentDistNeighbor == null || newDist < currentDistNeighbor) {
                    distances[neighbor.coordinates] = newDist
                    paths[neighbor.coordinates] = PointWithCoordinates(current, get(current))
                }
                queue.add(neighbor.coordinates to newDist)
            }
        }
        val path = mutableListOf<PointWithCoordinates>()
        var current = PointWithCoordinates(to, get(to))
        while (current.coordinates != from) {
            path.add(current)
            current = paths[current.coordinates]!!
        }

        return Path(path.reversed(), distances[to]!!)
    }
}

fun findShortestPath(lines: Sequence<String>): Int {
    var start: Coordinates? = null
    var end: Coordinates? = null
    val map = sequence {
        var currentWidth: Int? = null

        for ((y, line) in lines.withIndex()) {
            check(currentWidth == null || currentWidth == line.length)
            currentWidth = line.length
            yield(
                sequence {
                    for ((x, char) in line.withIndex()) {
                        when (char) {
                            'S' -> {
                                require(start == null)
                                start = Coordinates(x, y)
                            }
                            'E' -> {
                                require(end == null)
                                end = Coordinates(x, y)
                            }
                        }
                        yield(Point(char))
                    }
                }.toList()
            )

        }
    }.toList()

    require(start != null && end != null)

    val heightMap = HeightMap(map)

    return heightMap.shortestPath(start!!, end!!).weight
}