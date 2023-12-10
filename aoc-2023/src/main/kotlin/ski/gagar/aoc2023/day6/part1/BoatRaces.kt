package ski.gagar.aoc2023.day6.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

private fun Double.isIntegral() = this % 1.0 == 0.0

/**
 * Like regular ceil() but rounds up integer to next integer,
 * e. g. 20.0.forceCeil() would be 3.0
 */
private fun Double.forceCeil() =
    if (this.isIntegral()) (this + 1).toLong() else ceil(this).toLong()
/**
 * Like regular floor() but rounds down integer to previous integer,
 * e. g. 20.0.forceCeil() would be 1.0
 */
private fun Double.forceFloor() =
    if (this.isIntegral()) (this - 1).toLong() else floor(this).toLong()

data class BoatRace(val time: Long, val distanceHighScore: Long) {
    private val roots by lazy {
        // the equation for distance is the following:
        // (time - timeHold) * timeHold > distanceHighScore
        // (because timeHold would also be a speed)
        // We're gonna solve this inequation for timeHold and find
        // two roots, the timeHold between these two roots are winning timeHolds
        // The inequation can be found as follows:
        // - timeHold^2 + time * timeHold - distanceHighScore > 0
        // or
        // timeHold ^ 2 - time * timeHold + distanceHighScore > 0
        // This is a parabola who is less than zero in-between its roots
        // And below we're gonna find them

        val discriminant = time * time - 4 * 1 * distanceHighScore

        val left = (time.toDouble() - sqrt(discriminant.toDouble())) / 2.0
        val right = (time.toDouble() + sqrt(discriminant.toDouble())) / 2.0

        left to right
    }

    val minWinningHoldTime by lazy {
        // Force because we're solving strict inequation, e. g.
        // an integer which is a zero of target function is not a winning hold time
        roots.first.forceCeil()
    }

    val maxWinningHoldTime by lazy {
        // Force because we're solving strict inequation, e. g.
        // an integer which is a zero of target function is not a winning hold time
        roots.second.forceFloor()
    }

    val nWinningOptions: Long
        get() = maxWinningHoldTime - minWinningHoldTime + 1
}

object BoatRacesParser {
    private const val CAPITAL_TIME = "Time"
    private const val CAPITAL_DISTANCE = "Distance"
    private const val COLON = ":"
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TERMINALS = Terminals.operators(COLON, NL, NL_WIN)
        .words(Scanners.IDENTIFIER)
        .keywords(
            CAPITAL_TIME,
            CAPITAL_DISTANCE
        ).build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )
    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { it.toLong() }
    private val INTEGER_LIST = INTEGER.many()

    private val TIME_LIST = Parsers.sequence(
        TERMINALS.token(CAPITAL_TIME),
        TERMINALS.token(COLON),
        INTEGER_LIST
    ) { _, _, ints ->
        ints
    }
    private val DISTANCE_LIST = Parsers.sequence(
        TERMINALS.token(CAPITAL_DISTANCE),
        TERMINALS.token(COLON),
        INTEGER_LIST
    ) { _, _, ints ->
        ints
    }
    private val RACES = Parsers.sequence(
        TIME_LIST,
        NEWLINE.many1(),
        DISTANCE_LIST,
        NEWLINE.many()
    ) { times, _, distances, _ ->
        require(times.size == distances.size)
        times.asSequence().zip(distances.asSequence()).map {(time, distance) ->
            BoatRace(time, distance)
        }.toList()
    }

    fun parse(input: String) = RACES.from(TOKENIZER, WHITESPACES).parse(input)
}

fun multiplyOfNWinningOptions(input: String) =
    BoatRacesParser.parse(input).asSequence().map {
        it.nWinningOptions
    }.fold(1L) { a, b -> a * b }
