package ski.gagar.aoc2022.day14.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day14.part1.Point
import ski.gagar.aoc2022.day14.part1.RockLinesParser
import ski.gagar.aoc2022.day14.part1.RockPath
import kotlin.math.sign

class Cave(rockPaths: List<RockPath>, val sandSource: Point, val extraDepth: Int = 2) {
    private val rocks: Set<Point>
    private val sand: MutableSet<Point> = mutableSetOf()
    val depth: Int

    init {
        rocks = initRocks(rockPaths)
        depth = rocks.maxOf { it.y } + extraDepth
    }

    private fun initRocks(lines: List<RockPath>): Set<Point> {
        val rocks = mutableSetOf<Point>()

        for (line in lines) {
            var from = line.points.first()

            rocks.add(from)

            for (to in line.points.asSequence().drop(1)) {
                val dirX = (to.x - from.x).sign
                val dirY = (to.y - from.y).sign

                require((dirX != 0) xor (dirY != 0))

                while (from != to) {
                    from = Point(from.x + dirX, from.y + dirY)
                    rocks.add(from)
                }
            }
        }
        return rocks
    }

    private fun isFree(point: Point) = point !in rocks && point !in sand
    private fun isFree(x: Int, y: Int) = isFree(Point(x, y))

    val nSand
        get() = sand.size

    fun sandFall(): Boolean {
        if (!isFree(sandSource)) {
            return false
        }

        var point = sandSource

        while (true) {
            if (point.y == depth - 1) {
                sand.add(point)
                return true
            }

            val bottom = Point(point.x, point.y + 1)

            if (isFree(bottom)) {
                point = bottom
                continue
            }

            val bottomLeft = Point(point.x - 1, point.y + 1)

            if (isFree(bottomLeft)) {
                point = bottomLeft
                continue
            }

            val bottomRight = Point(point.x + 1, point.y + 1)

            if (isFree(bottomRight)) {
                point = bottomRight
                continue
            }

            sand.add(point)
            return true
        }
    }

    fun sandFallWhileNotFull(): Int {
        var count = 0
        while (true) {
            if (!sandFall()) {
                break
            }
            count++
        }

        return count
    }
}

fun simulate(lines: Sequence<String>, sandSource: Point = Point(500, 0)): Int {
    val rockLines = lines.map { RockLinesParser.parse(it) }.toList()
    val cave = Cave(rockLines, sandSource)

    return cave.sandFallWhileNotFull()
}
