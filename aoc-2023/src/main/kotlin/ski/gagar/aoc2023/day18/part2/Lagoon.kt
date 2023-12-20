package ski.gagar.aoc2023.day18.part2

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns
import java.math.BigInteger

enum class Direction(val str: String) {
    UP("U") {
        override val opposite: Direction
            get() = DOWN
    },
    RIGHT("R") {
        override val opposite: Direction
            get() = LEFT
    },
    LEFT("L") {
        override val opposite: Direction
            get() = RIGHT
    },
    DOWN("D") {
        override val opposite: Direction
            get() = UP
    };

    abstract val opposite: Direction

    companion object {
        private val byStr = entries.associateBy { it.str }

        fun from(str: String) = byStr[str] ?: throw IllegalArgumentException("Unknown direction $str")
    }
}

data class Coordinates(val x: Long, val y: Long) {
    fun left(amount: Long) = Coordinates(x - amount, y)
    val left
        get() = left(1)
    fun right(amount: Long) = Coordinates(x + amount, y)
    val right
        get() = right(1)
    fun up(amount: Long) = Coordinates(x, y - amount)
    val up
        get() = Coordinates(x, y - 1)
    fun down(amount: Long) = Coordinates(x, y + amount)
    val down
        get() = down(1)

    fun toThe(dir: Direction, amount: Long) = when (dir) {
        Direction.UP -> up(amount)
        Direction.LEFT -> left(amount)
        Direction.DOWN -> down(amount)
        Direction.RIGHT -> right(amount)
    }

}

data class DigCommand(val direction: Direction, val amount: Long)

data class Polygon(val points: List<Coordinates>) {
    init {
        var prev: Coordinates? = null

        for (point in points) {
            if (null != prev) {
                require(prev.x == point.x || prev.y == point.y) {
                    "Only horizontal and vertical lines are supported"
                }
            }
            prev = point
        }
    }

    private val areaPairs
        get() = sequence {
            var prev: Coordinates? = null

            for (point in points) {
                if (null != prev) {
                    yield(prev to point)
                }
                prev = point
            }

            if (null != prev) {
                yield(prev to points.first())
            }
        }

    val area: BigInteger
        get() = areaPairs.sumOf { (a, b) ->
            BigInteger.valueOf(a.x) * BigInteger.valueOf(b.y) - BigInteger.valueOf(b.x) * BigInteger.valueOf(a.y)
        }.abs() / BigInteger.TWO
}

data class Polygons(val inner: Polygon, val outer: Polygon)

enum class CellAngle {
    TL {
        override val opposite: CellAngle
            get() = BR
    }, TR {
        override val opposite: CellAngle
            get() = BL
    }, BL {
        override val opposite: CellAngle
            get() = TR
    }, BR {
        override val opposite: CellAngle
            get() = TL
    };

    abstract val opposite: CellAngle;
}

fun Coordinates.cellToGrid(angle: CellAngle) = when (angle) {
    CellAngle.TL -> this
    CellAngle.TR -> Coordinates(x + 1, y)
    CellAngle.BL -> Coordinates(x, y + 1)
    CellAngle.BR -> Coordinates(x + 1, y + 1)
}

fun cellAngleLhs(first: Direction, second: Direction): CellAngle = when {
    first == Direction.UP && second == Direction.RIGHT -> CellAngle.TL
    first == Direction.UP && second == Direction.LEFT -> CellAngle.BL
    first == Direction.DOWN && second == Direction.RIGHT -> CellAngle.TR
    first == Direction.DOWN && second == Direction.LEFT -> CellAngle.BR
    first == Direction.RIGHT && second == Direction.UP -> CellAngle.TL
    first == Direction.RIGHT && second == Direction.DOWN -> CellAngle.TR
    first == Direction.LEFT && second == Direction.UP -> CellAngle.BL
    first == Direction.LEFT && second == Direction.DOWN -> CellAngle.BR
    else -> throw IllegalArgumentException("$first and $second are not supported")
}

fun cellAngleRhs(first: Direction, second: Direction): CellAngle = cellAngleLhs(first, second).opposite

private fun List<DigCommand>.polygon(
    angleSelector: (Direction, Direction) -> CellAngle,
    start: Coordinates = Coordinates(0,0)): Polygon {
    require(this.size >= 4)

    var current = start
    var prevDirection = last().direction

    val result = mutableListOf<Coordinates>()

    for (cmd in this) {
        val gridCoord = current.cellToGrid(angleSelector(prevDirection, cmd.direction))
        result.add(gridCoord)
        current = current.toThe(cmd.direction, cmd.amount)
        prevDirection = cmd.direction
    }

    require(current == start) {
        "Not a polygon"
    }
    return Polygon(result)
}

fun List<DigCommand>.polygons(start: Coordinates = Coordinates(0,0)): Polygons {
    val lhs = polygon(::cellAngleLhs, start)
    val rhs = polygon(::cellAngleRhs, start)

    val rhsArea = rhs.area
    val lhsArea = lhs.area

    return if (lhsArea > rhsArea) {
        Polygons(inner = rhs, outer = lhs)
    } else {
        Polygons(inner = lhs, outer = rhs)
    }
}

object DigCommandsParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private const val L_PAREN = "("
    private const val R_PAREN = ")"

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val HEX_INTEGER = Patterns.string("#").next(Patterns.many1(CharPredicates.IS_HEX_DIGIT))
    private val HEX_TOKENIZER =
        HEX_INTEGER.toScanner("hexadecimal integer").source().map { it.removePrefix("#").toInt(16) }

    private val TERMINALS = Terminals.operators(L_PAREN, R_PAREN, NL, NL_WIN)
        .words(Scanners.IDENTIFIER)
        .build()

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        HEX_TOKENIZER,
        INT_TOKENIZER,
    )

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val HEX = Parsers.tokenType(Integer::class.java, "hexadecimal integer").map { int ->
        int.toInt()
    }

    private val DIRECTION = Terminals.identifier().map { Direction.from(it) }
    private val AMOUNT = Terminals.IntegerLiteral.PARSER.map { it.toLong() }
    private val HEX_COMMAND = Parsers.sequence(
        TERMINALS.token(L_PAREN),
        HEX,
        TERMINALS.token(R_PAREN)
    ) { _, colorHex, _ ->
        colorHex
    }

    private val COMMAND = Parsers.sequence(DIRECTION, AMOUNT, HEX_COMMAND) { _, _, hexCmd ->
        val threeBytes = hexCmd and 0xffffff
        val directionCode = threeBytes and 0xf
        val amount = (threeBytes shr 4).toLong()

        val dir = when (directionCode) {
            0 -> Direction.RIGHT
            1 -> Direction.DOWN
            2 -> Direction.LEFT
            3 -> Direction.UP
            else -> throw IllegalArgumentException("Illegal direction code $directionCode")
        }

        DigCommand(dir, amount)
    }

    private val COMMANDS = COMMAND.sepBy(NEWLINE)

    private val COMMANDS_WITH_NL =
        Parsers.sequence(NEWLINE.many(), COMMANDS, NEWLINE.many()) { _, cmds, _ ->
            cmds
        }

    fun parse(input: String) = COMMANDS_WITH_NL.from(TOKENIZER, WHITESPACES).parse(input, Parser.Mode.DEBUG)
}

fun getFilledArea(input: String): BigInteger = DigCommandsParser.parse(input).polygons().outer.area