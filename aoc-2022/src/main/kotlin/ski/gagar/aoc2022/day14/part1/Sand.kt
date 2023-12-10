package ski.gagar.aoc2022.day14.part1

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.Tokens
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day13.part1.getSumOfIndices
import kotlin.math.sign


data class Point(val x: Int, val y: Int)

data class RockPath(val points: List<Point>) {
    init {
        require(points.isNotEmpty())
    }
}

class Cave(rockPaths: List<RockPath>, val sandSource: Point) {
    private val rocks: Set<Point>
    private val sand: MutableSet<Point> = mutableSetOf()
    val depth: Int

    init {
        rocks = initRocks(rockPaths)
        depth = rocks.maxOf { it.y }
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
        require(isFree(sandSource)) {
            "Sand source is clogged"
        }

        var point = sandSource

        while (true) {
            if (point.y > depth) {
                return false
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

    fun sandFallWhileResting(): Int {
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

object RockLinesParser {
    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val TERMINALS =
        Terminals.operators("->", ",")
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.optional()
    private val TOKENIZER = TERMINALS.tokenizer().cast<Tokens.Fragment>().or(INT_TOKENIZER)

    private val INT: Parser<Int> = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val POINT = Parsers.sequence(
        INT,
        TERMINALS.token(","),
        INT
    ) { x, _, y ->
        Point(x, y)
    }

    private val PATH = POINT.sepBy(TERMINALS.token("->")).map {
        RockPath(it)
    }

    fun parse(line: String) = PATH.from(TOKENIZER, WHITESPACES).parse(line)

    fun dbg(line: String) = POINT.from(TOKENIZER, WHITESPACES).parse(line)
}


fun simulate(lines: Sequence<String>, sandSource: Point = Point(500, 0)): Int {
    val rockLines = lines.map { RockLinesParser.parse(it) }.toList()
    val cave = Cave(rockLines, sandSource)

    return cave.sandFallWhileResting()
}
