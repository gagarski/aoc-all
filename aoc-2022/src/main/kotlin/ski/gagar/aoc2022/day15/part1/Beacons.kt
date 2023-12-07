package ski.gagar.aoc2022.day15.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.Tokens
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day11.part1.MonkeyParser
import java.util.BitSet
import kotlin.math.absoluteValue

data class Coordinates(val x: Int, val y: Int) {
    fun manhattanDistanceTo(other: Coordinates) =
        (x - other.x).absoluteValue + (y - other.y).absoluteValue
}

data class ClosestBeacon(val sensor: Coordinates, val beacon: Coordinates) {
    val manhattanDistance by lazy {
        sensor.manhattanDistanceTo(beacon)
    }

    fun shadowAtRow(y: Int): IntRange? {
        val yDist = (sensor.y - y).absoluteValue
        val diff = manhattanDistance - yDist

        if (diff < 0)
            return null

        val left = if (beacon.x == sensor.x - diff) sensor.x - diff + 1 else sensor.x - diff
        val right = if (beacon.x == sensor.x + diff) sensor.x + diff - 1 else sensor.x + diff

        if (left > right)
            return null

        return left..right
    }

    fun shadowAtColumn(x: Int): IntRange? {
        val xDist = (sensor.x - x).absoluteValue
        val diff = manhattanDistance - xDist
        if (diff < 0)
            return null
        val top = if (beacon.y == sensor.y - diff) sensor.y - diff + 1 else sensor.y - diff
        val bottom = if (beacon.y == sensor.y + diff) sensor.y + diff - 1 else sensor.y + diff

        if (top > bottom)
            return null

        return top..bottom
    }
}

object BeaconParser {
    private val CAPITAL_SENSOR = "Sensor"
    private val AT = "at"
    private val X = "x"
    private val Y = "y"
    private val CLOSEST = "closest"
    private val BEACON = "beacon"
    private val IS = "is"

    private val EQ = "="
    private val MINUS = "-"
    private val COMMA = ","
    private val COLON = ":"
    private val NL = "\n"
    private val NL_WIN = "\r\n"


    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val TERMINALS = Terminals
        .operators(EQ, MINUS, COMMA, COLON, NL, NL_WIN)
        .words(Scanners.IDENTIFIER)
        .keywords(
            CAPITAL_SENSOR,
            AT,
            X,
            Y,
            CLOSEST,
            BEACON,
            IS
        )
        .build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val POS_INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toInt().also {
            check(it >= 0)
        }
    }

    private val NEG_INTEGER = Parsers.sequence(
        TERMINALS.token(MINUS),
        POS_INTEGER
    ).map { -it }

    private val INTEGER = Parsers.or(
        POS_INTEGER,
        NEG_INTEGER
    )

    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private val TOKENIZER = TERMINALS.tokenizer().cast<Tokens.Fragment>().or(INT_TOKENIZER)

    private val X_COORD = Parsers.sequence(
        TERMINALS.token(X),
        TERMINALS.token(EQ),
        INTEGER
    )

    private val Y_COORDS = Parsers.sequence(
        TERMINALS.token(Y),
        TERMINALS.token(EQ),
        INTEGER
    )

    private val COORDS = Parsers.sequence(
        X_COORD,
        TERMINALS.token(COMMA),
        Y_COORDS
    ) { x, _, y ->
        Coordinates(x, y)
    }

    private val SENSOR_COORDS = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_SENSOR, AT),
        COORDS
    )

    private val CLOSEST_BEACON_COORDS = Parsers.sequence(
        TERMINALS.phrase(CLOSEST, BEACON, IS, AT),
        COORDS
    )

    private val CLOSEST_BEACON = Parsers.sequence(
        SENSOR_COORDS,
        TERMINALS.token(COLON),
        CLOSEST_BEACON_COORDS
    ) { sensor, _, beacon ->
        ClosestBeacon(sensor, beacon)
    }

    private val CLOSEST_BEACONS = CLOSEST_BEACON.sepBy(NEWLINE)

    private val CLOSEST_BEACONS_WITH_NL = Parsers.sequence(
        NEWLINE.many(),
        CLOSEST_BEACONS,
        NEWLINE.many()
    ) { _, it, _ ->
        it
    }

    fun parse(str: String) = CLOSEST_BEACONS.from(TOKENIZER, WHITESPACES).parse(str)
}

fun shadowSizeForRow(text: String, row: Int = 2000000): Int {
    val shadows = BeaconParser.parse(text).asSequence().map { it.shadowAtRow(row) }.filterNotNull().toList()
    val min = shadows.minOf { it.first }
    val bitSet = BitSet()

    for (shadow in shadows) {
        bitSet.set(shadow.first - min, shadow.last - min + 1)
    }

    return bitSet.cardinality()
}
fun day15Part1() {
    println("day15/part1/beacons: ${
        shadowSizeForRow(getResourceAsStream("/ski.gagar.aoc.aoc2022.day15/beacons.txt").bufferedReader().readText())
    }")
}
