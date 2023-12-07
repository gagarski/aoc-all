package ski.gagar.aoc2023.day4.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc.util.pow


data class ScratchCard(val id: Int, val winning: Set<Int>, val present: Set<Int>) {
    val intersectSize: Int
    val points: Long

    init {
        intersectSize = winning.intersect(present).size
        points = if (intersectSize == 0) 0L else 2L pow (intersectSize - 1)
    }
}

object ScratchCardParser {
    private const val CAPITAL_CARD = "Card"
    private const val COLON = ":"
    private const val PIPE = "|"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TERMINALS =
        Terminals.operators(COLON, PIPE)
            .words(Scanners.IDENTIFIER)
            .keywords(CAPITAL_CARD)
            .build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val CARD_ID = Parsers.sequence(
        TERMINALS.token(CAPITAL_CARD),
        INTEGER
    ) { _, id -> id}

    private val NUM_LIST = INTEGER.many().map { it.toSet() }

    private val CARD = Parsers.sequence(
        CARD_ID,
        TERMINALS.token(COLON),
        NUM_LIST,
        TERMINALS.token(PIPE),
        NUM_LIST
    ) { id, _, winning, _, present ->
        ScratchCard(id, winning, present)
    }

    fun parse(input: String) = CARD.from(TOKENIZER, WHITESPACES).parse(input)
}

private fun sumPoints(lines: Sequence<String>) =
    lines.map {
        ScratchCardParser.parse(it).points
    }.sum()

fun day4Part1() {
    println(
        "day4/part1/scratchcards: ${
            sumPoints(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day4/scratchcards.txt").bufferedReader().lineSequence()
            )
        }"
    )
}
