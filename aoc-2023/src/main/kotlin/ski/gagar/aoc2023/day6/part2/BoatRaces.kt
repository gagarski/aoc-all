package ski.gagar.aoc2023.day6.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2023.day6.part1.BoatRace

fun List<Long>.collapse() =
    this.joinToString("").toLong()

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

    private val TIME = Parsers.sequence(
        TERMINALS.token(CAPITAL_TIME),
        TERMINALS.token(COLON),
        INTEGER_LIST
    ) { _, _, ints ->
        ints.collapse()
    }
    private val DISTANCE = Parsers.sequence(
        TERMINALS.token(CAPITAL_DISTANCE),
        TERMINALS.token(COLON),
        INTEGER_LIST
    ) { _, _, ints ->
        ints.collapse()
    }
    private val RACES = Parsers.sequence(
        TIME,
        NEWLINE.many1(),
        DISTANCE,
        NEWLINE.many()
    ) { time, _, distance, _ ->
        BoatRace(time, distance)
    }

    fun parse(input: String) = RACES.from(TOKENIZER, WHITESPACES).parse(input)
}

fun multiplyOfNWinningOptions(input: String) =
    BoatRacesParser.parse(input).nWinningOptions

fun day6Part2() {
    println(
        "day6/part1/boatRaces: ${
            multiplyOfNWinningOptions(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day6/boatRaces.txt").bufferedReader().readText()
            )
        }"
    )
}
