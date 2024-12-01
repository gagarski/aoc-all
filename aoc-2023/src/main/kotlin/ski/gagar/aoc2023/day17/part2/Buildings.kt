package ski.gagar.aoc2023.day17.part2

import ski.gagar.aoc.util.graphs.Edge
import ski.gagar.aoc.util.graphs.GraphBuilder
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Coordinates(val x: Int, val y: Int) {
    val left
        get() = Coordinates(x - 1, y)
    val right
        get() = Coordinates(x + 1, y)
    val up
        get() = Coordinates(x, y - 1)
    val down
        get() = Coordinates(x, y + 1)

    fun toThe(dir: Direction) = when (dir) {
        Direction.UP -> up
        Direction.LEFT -> left
        Direction.DOWN -> down
        Direction.RIGHT -> right
    }

    fun relationTo(other: Coordinates) = when (other) {
        up -> Direction.UP
        left -> Direction.LEFT
        down -> Direction.DOWN
        right -> Direction.RIGHT
        else -> null
    }
}

enum class Direction(val char: Char) {
    UP('^'),
    LEFT('<'),
    DOWN('v'),
    RIGHT('>');
}

class HeatMap(val heats: List<List<Int>>) {
    val width: Int
    val height: Int

    init {
        height = heats.size
        require(height > 0)
        width = heats[0].size
        require(width > 0)
        require(heats.all { it.size == width })
    }

    operator fun get(coords: Coordinates) = heats[coords.y][coords.x]
    operator fun contains(coords: Coordinates) =
        coords.y in 0 until height && coords.x in 0 until width

    private fun List<Edge<Coordinates>>.sumWeight() =
        sumOf { it.weight }

    fun shortestPath(from: Coordinates, to: Coordinates,
                     minStraight: Int = 4,
                     maxStraight: Int = 10): List<Edge<Coordinates>>? {
        val shortestHv = shortestPath(from, to, minStraight, maxStraight, this::weightHv, this::isOnLastMileHv)
        val shortestVh = shortestPath(from, to, minStraight, maxStraight, this::weightVh, this::isOnLastMileVh)


        val hvLen = shortestHv?.sumWeight()
        val vhLen = shortestVh?.sumWeight()

        if (hvLen == null && vhLen == null) {
            return null
        }

        if (hvLen == null) {
            return shortestVh
        }

        if (vhLen == null) {
            return shortestHv
        }

        return if (hvLen < vhLen) {
            shortestHv
        } else {
            shortestVh
        }

    }


    private fun shortestPath(from: Coordinates,
                             to: Coordinates,
                             minStraight: Int = 4,
                             maxStraight: Int = 10,
                             weight: (Coordinates, Coordinates) -> Int,
                             isOnLastMile: (Coordinates, Coordinates, Int, Int) -> Boolean): List<Edge<Coordinates>>? {
        val bld = GraphBuilder<Coordinates>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                bld.addVertex(Coordinates(x, y))
            }
        }
        for (y in 0 until height) {
            for (x in 0 until width) {
                val point = Coordinates(x, y)
                bld.connect(point, minStraight, maxStraight, weight)

                if (isOnLastMile(point, to, minStraight, maxStraight)) {
                    bld.addEdge(point, to, weight(point, to))
                }
            }
        }

        return bld.build().shortestPaths(from).to(to)
    }

    private fun GraphBuilder<Coordinates>.connect(
        coordinates: Coordinates,
        minStraight: Int,
        maxStraight: Int,
        weight: (Coordinates, Coordinates) -> Int
    ) {
        for (dy in minStraight..maxStraight) {
            for (dx in minStraight..maxStraight) {
                val lt = Coordinates(coordinates.x - dx, coordinates.y - dy)
                val rt = Coordinates(coordinates.x + dx, coordinates.y - dy)
                val lb = Coordinates(coordinates.x - dx, coordinates.y + dy)
                val rb = Coordinates(coordinates.x + dx, coordinates.y + dy)

                if (lt in this@HeatMap) {
                    addEdge(coordinates, lt, weight(coordinates, lt))
                }
                if (rt in this@HeatMap) {
                    addEdge(coordinates, rt, weight(coordinates, rt))
                }
                if (lb in this@HeatMap) {
                    addEdge(coordinates, lb, weight(coordinates, lb))
                }
                if (rb in this@HeatMap) {
                    addEdge(coordinates, rb, weight(coordinates, rb))
                }
            }
        }
    }

    private fun weightHv(from: Coordinates, to: Coordinates): Int {
        val signX = (to.x - from.x).sign
        val signY = (to.y - from.y).sign

        var weight = 0
        var curX = from.x

        while (curX != to.x) {
            curX += signX
            weight += this[Coordinates(curX, from.y)]
        }

        var curY = from.y
        while (curY != to.y) {
            curY += signY
            weight += this[Coordinates(to.x, curY)]
        }

        return weight
    }

    private fun weightVh(from: Coordinates, to: Coordinates): Int {
        val signX = (to.x - from.x).sign
        val signY = (to.y - from.y).sign

        var weight: Int = 0
        var curY = from.y

        while (curY != to.y) {
            curY += signY
            weight += this[Coordinates(from.x, curY)]
        }

        var curX = from.x
        while (curX != to.x) {
            curX += signX
            weight += this[Coordinates(curX, to.y)]
        }

        return weight
    }

    private fun isOnLastMileHv(coords: Coordinates, to: Coordinates, minStraight: Int, maxStraight: Int) =
        coords.y == to.y && (to.x - coords.x).absoluteValue in minStraight..maxStraight

    private fun isOnLastMileVh(coords: Coordinates, to: Coordinates, minStraight: Int, maxStraight: Int) =
        coords.x == to.x && (to.y - coords.y).absoluteValue in minStraight..maxStraight

    override fun toString() = heats
        .joinToString("\n") {
            it.joinToString("")
        }

    companion object {
        fun from(lines: Sequence<String>) =
            lines.map { line ->
                line.map { ch ->
                    ch.digitToInt()
                }
            }.toList().let { HeatMap(it) }
    }
}

fun minHeatLoss(lines: Sequence<String>): Int? {
    val map = HeatMap.from(lines)
    val from = Coordinates(0, 0)
    val to = Coordinates(map.width - 1, map.height - 1)
    val shortestPath = map.shortestPath(
        from, to)
    return shortestPath?.sumOf { it.weight }
}