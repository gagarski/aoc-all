package ski.gagar.aoc2023.day18.part1

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns

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

data class Coordinates(val x: Int, val y: Int) {
    val left
        get() = Coordinates(x - 1, y)
    val right
        get() = Coordinates(x + 1, y)
    val up
        get() = Coordinates(x, y - 1)
    val down
        get() = Coordinates(x, y + 1)

    val neighbors
        get() = listOf(up, right, down, left)

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

data class RgbColor(val r: UByte, val g: UByte, val b: UByte) {
    companion object {
        private fun Int.lastByte() = (this and 0xff).toUByte()

        fun fromInt(int: Int): RgbColor {
            var shifted = int
            val b = shifted.lastByte()
            shifted = shifted shr 8
            val g = shifted.lastByte()
            shifted = shifted shr 8
            val r = shifted.lastByte()
            return RgbColor(r, g, b)
        }
    }
}

data class DigCommand(val direction: Direction, val amount: Int, val color: RgbColor)

class DigField {
    private val canvas = mutableMapOf<Coordinates, RgbColor>()
    var minX: Int = 0
        private set
    var maxX: Int = -1
        private set
    var minY: Int = 0
        private set
    var maxY: Int = -1
        private set
    val width: Int
        get() = maxX - minX + 1
    val height: Int
        get() = maxY - minY + 1

    operator fun contains(coordinates: Coordinates) =
        coordinates.x in minX..maxX && coordinates.y in minY..maxY

    operator fun get(coordinates: Coordinates) = canvas[coordinates]

    operator fun set(coordinates: Coordinates, color: RgbColor?) {
        if (null == color) {
            remove(coordinates)
        } else {
            canvas[coordinates] = color
            expand(coordinates)
        }
    }

    fun line(start: Coordinates, direction: Direction, length: Int, color: RgbColor?): Coordinates {
        var curr = start

        for (i in 0 until length) {
            if (null == color) {
                canvas.remove(curr)
            } else {
                canvas[curr] = color
            }
            curr = curr.toThe(direction)
        }
        fitSize()
        return curr.toThe(direction.opposite)
    }

    fun remove(coordinates: Coordinates) = canvas.remove(coordinates).also {
        fitSize()
    }

    val size
        get() = canvas.size

    private fun fillImpl(from: Coordinates, color: RgbColor?,
                         paintPredicate: (Coordinates) -> Boolean = { true }): Int {
        if (from !in this) return 0

        val queue = ArrayDeque<Coordinates>()
        val startColor = canvas[from]

        queue.add(from)

        val filled = mutableSetOf<Coordinates>()
        var res = 0

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()

            if (curr in filled) continue
            if (curr !in this) continue

            val currColor = canvas[curr]

            if (currColor != startColor) continue

            if (paintPredicate(curr)) {
                if (color != null)
                    canvas[curr] = color
                else
                    canvas.remove(curr)
                res++
            }

            filled.add(curr)

            for (neighbor in curr.neighbors) {
                queue.add(neighbor)
            }
        }

        if (null == color) {
            fitSize()
        }

        return res
    }

    fun fill(from: Coordinates, color: RgbColor?) = fillImpl(from, color)

    fun fillFromOutside(color: RgbColor?): Int {
        val oldMinX = minX
        val oldMaxX = maxX
        val oldMinY = minY
        val oldMaxY = maxY

        minX -= 1
        maxX += 1
        minY -= 1
        maxY += 1

        val res = fillImpl(Coordinates(minX, minY), color) {
            it.x in oldMinX..oldMaxX && it.y in oldMinY..oldMaxY
        }

        minX = oldMinX
        maxX = oldMaxX
        minY = oldMinY
        maxY = oldMaxY

        return res
    }

    val fullArea
        get() = width * height

    val insideArea
        get() = fullArea - fillFromOutside(null)

    private fun expand(coordinates: Coordinates) {
        if (coordinates.x < minX) {
            minX = coordinates.x
        }

        if (coordinates.x > maxX) {
            maxX = coordinates.x
        }

        if (coordinates.y < minY) {
            minY = coordinates.y
        }

        if (coordinates.y > maxY) {
            maxY = coordinates.y
        }
    }

    private fun fitSize() {
        minX = canvas.keys.minOfOrNull { it.x } ?: 0
        maxX = canvas.keys.maxOfOrNull { it.x } ?: -1
        minY = canvas.keys.minOfOrNull { it.y } ?: 0
        maxY = canvas.keys.maxOfOrNull { it.y } ?: -1
    }


    override fun toString(): String = buildString {
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (Coordinates(x, y) in canvas) {
                    append("#")
                } else {
                    append(".")
                }
            }
            append("\n")
        }
    }
}

fun DigField.exec(cmd: DigCommand, start: Coordinates = Coordinates(this.minX, this.minY)) =
    line(start.toThe(cmd.direction), cmd.direction, cmd.amount, cmd.color)

fun DigField.exec(cmds: List<DigCommand>, start: Coordinates = Coordinates(this.minX, this.minY)): Coordinates {
    var lastEnd = start
    for (cmd in cmds) {
        lastEnd = exec(cmd, lastEnd)
    }
    return lastEnd
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
    private val AMOUNT = Terminals.IntegerLiteral.PARSER.map { it.toInt() }
    private val COLOR = Parsers.sequence(
        TERMINALS.token(L_PAREN),
        HEX,
        TERMINALS.token(R_PAREN)
    ) { _, colorHex, _ ->
        RgbColor.fromInt(colorHex)
    }

    private val COMMAND = Parsers.sequence(DIRECTION, AMOUNT, COLOR) { dir, amount, color ->
        DigCommand(dir, amount, color)
    }

    private val COMMANDS = COMMAND.sepBy(NEWLINE)

    private val COMMANDS_WITH_NL =
        Parsers.sequence(NEWLINE.many(), COMMANDS, NEWLINE.many()) { _, cmds, _ ->
            cmds
        }

    fun parse(input: String) = COMMANDS_WITH_NL.from(TOKENIZER, WHITESPACES).parse(input, Parser.Mode.DEBUG)
}

fun getFilledArea(input: String): Int {
    val field = DigField()
    val cmds = DigCommandsParser.parse(input)
    field.exec(cmds)
    return field.insideArea
}