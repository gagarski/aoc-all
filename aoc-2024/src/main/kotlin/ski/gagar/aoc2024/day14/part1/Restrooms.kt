package ski.gagar.aoc2024.day14.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import java.math.BigInteger

data class Coordinates(val column: BigInteger, val row: BigInteger)

data class Robot(val start: Coordinates, val velocity: Coordinates)

object RobotsParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private const val P = "p"
    private const val V = "v"
    private const val EQ = "="
    private const val COMMA = ","
    private const val MINUS = "-"

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER

    private val TERMINALS = Terminals
        .operators(EQ, COMMA, MINUS, NL, NL_WIN)
        .words(Scanners.IDENTIFIER)
        .keywords(P, V)
        .build()

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER
    )

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toInt()
    }

    private val NEG_INTEGER = Parsers.sequence(TERMINALS.token(MINUS), INTEGER) { _, int ->
        -int
    }

    private val POS_OR_NEG_INTEGER = Parsers.or(INTEGER, NEG_INTEGER)

    private val PAIR_OF_INT = Parsers.sequence(POS_OR_NEG_INTEGER, TERMINALS.token(COMMA), POS_OR_NEG_INTEGER) { first, _, second ->
        Coordinates(first.toBigInteger(), second.toBigInteger())
    }

    private val P_DECL = Parsers.sequence(TERMINALS.token(P), TERMINALS.token(EQ), PAIR_OF_INT) { _, _, ints ->
        ints
    }

    private val V_DECL = Parsers.sequence(TERMINALS.token(V), TERMINALS.token(EQ), PAIR_OF_INT) { _, _, ints ->
        ints
    }

    private val ROBOT = Parsers.sequence(P_DECL, V_DECL) { p, v ->
        Robot(p, v)
    }

    private val ROBOTS = Parsers.sequence(
        NEWLINE.many(),
        ROBOT.sepBy(NEWLINE.many1()),
        NEWLINE.many()
    ) { _, r, _-> r }

    fun parse(input: String) = ROBOTS.from(TOKENIZER, WHITESPACES).parse(input)
}

fun Coordinates.runNaive(field: Coordinates, velocity: Coordinates): Coordinates {
    var row = (this.row + velocity.row)
    var column = (this.column + velocity.column)

    if (row < BigInteger.ZERO) {
        row += field.row
    } else if (row >= field.row) {
        row -= field.row
    }

    if (column < BigInteger.ZERO) {
        column += field.column
    } else if (column >= field.column) {
        column -= field.column
    }


    return Coordinates(column, row)
}


fun Coordinates.runNaive(steps: Int, velocity: Coordinates, field: Coordinates): Coordinates {
    var coord = this
    repeat(steps) {
        coord = coord.runNaive(field, velocity)
    }

    return coord
}

fun Robot.run(steps: Int, field: Coordinates): Coordinates {
    val row = (start.row + velocity.row * steps.toBigInteger()).mod(field.row)
    val column = (start.column + velocity.column * steps.toBigInteger()).mod(field.column)
    return Coordinates(column, row)
}

fun Int.toDigitOrPlus() =
    when (this) {
        in 0..9 -> '0' + this
        else -> '+'
    }

fun List<Coordinates>.draw(field: Coordinates) = buildString {
    val counts = this@draw.groupingBy { it }.eachCount()
    val midRow = field.row / BigInteger.TWO
    val midColumn = field.column / BigInteger.TWO
    for (row in 0 until field.row.toInt()) {
        for (col in 0 until field.column.toInt()) {
            val count: Int = counts[Coordinates(col.toBigInteger(), row.toBigInteger())] ?: 0
            when {
                row == midRow.toInt() || col == midColumn.toInt() -> append(" ")
                count == 0 -> append('.')
                else -> append(count.toDigitOrPlus())
            }
        }
        append('\n')
    }
}

private val F_EX = Coordinates(column = 11.toBigInteger(), row = 7.toBigInteger())
private val F_REAL = Coordinates(column = 101.toBigInteger(), row = 103.toBigInteger())

fun mulQuadrants(input: String, steps: Int = 100,
                 field: Coordinates = F_REAL
): Long {
    require(field.row % BigInteger.TWO == BigInteger.ONE)
    require(field.column % BigInteger.TWO == BigInteger.ONE)
    val robots = RobotsParser.parse(input)
//    val endCoords = robots.map { it.start.runNaive(steps, it.velocity, field) }
    val endCoords = robots.map { it.run(steps, field) }
    val midRow = field.row / BigInteger.TWO
    val midColumn = field.column / BigInteger.TWO

    val q1 = endCoords.count {
        it.row >= BigInteger.ZERO && it.row < midRow &&
                it.column >= BigInteger.ZERO && it.column < midColumn
    }.toLong()

    val q2 = endCoords.count {
        it.row >= BigInteger.ZERO && it.row < midRow &&
                it.column > midColumn && it.column < field.column
    }.toLong()

    val q3 = endCoords.count {
        it.row > midRow && it.row < field.row &&
                it.column >= BigInteger.ZERO && it.column < midColumn
    }.toLong()

    val q4 = endCoords.count {
        it.row > midRow && it.row < field.row &&
                it.column > midColumn && it.column < field.column
    }.toLong()

    return q1 * q2 * q3 * q4
}
