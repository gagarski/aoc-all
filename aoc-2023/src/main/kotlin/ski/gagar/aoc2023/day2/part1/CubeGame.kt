package ski.gagar.aoc2023.day2.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream


data class Round(val red: Int, val green: Int, val blue: Int) {
    init {
        require(red >= 0)
        require(green >= 0)
        require(blue >= 0)
    }

    fun isPossible(limits: Round) =
        this.red <= limits.red && this.green <= limits.green && this.blue <= limits.blue
}

data class Game(val id: Int, val rounds: List<Round>) {
    init {
        require(rounds.isNotEmpty())
    }

    fun isPossible(limits: Round) =
        rounds.all { it.isPossible(limits) }
}

object CubeGameParser {
    private const val CAPITAL_GAME = "Game"
    private const val BLUE = "blue";
    private const val GREEN = "green";
    private const val RED = "red";
    private const val SEMICOLON = ";"
    private const val COLON = ":"
    private const val COMMA = ","

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TERMINALS =
        Terminals.operators(SEMICOLON, COMMA, COLON)
            .words(Scanners.IDENTIFIER)
            .keywords(CAPITAL_GAME, BLUE, GREEN, RED)
            .build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val COLOR_PARSER = Parsers.or(
        TERMINALS.token(RED).map { Color.RED },
        TERMINALS.token(GREEN).map { Color.GREEN },
        TERMINALS.token(BLUE).map { Color.BLUE }
    )

    private val ROUND_PART = Parsers.sequence(
        INTEGER,
        COLOR_PARSER
    ) { amount, color -> RoundPart(color, amount) }

    private val ROUND =
        ROUND_PART.sepBy(TERMINALS.token(COMMA)).map {
            makeRound(it)
        }

    private val GAME_CONTENTS = ROUND.sepBy(TERMINALS.token(SEMICOLON)).map {
        it.toList()
    }

    private val GAME = Parsers.sequence(
        TERMINALS.token(CAPITAL_GAME),
        INTEGER,
        TERMINALS.token(COLON),
        GAME_CONTENTS
    ) { _, id, _, contents ->
        Game(id, contents)
    }

    private enum class Color {
        RED, GREEN, BLUE
    }

    private data class RoundPart(val color: Color, val amount: Int)

    private fun makeRound(parts: List<RoundPart>): Round {
        require(parts.isNotEmpty())
        require(parts.size <= 3)
        val presentColors = parts.asSequence().map { it.color to it.amount }.toMap()
        require(presentColors.size == parts.size)

        return Round(
            presentColors[Color.RED] ?: 0,
            presentColors[Color.GREEN] ?: 0,
            presentColors[Color.BLUE] ?: 0
        )
    }

    fun parse(input: String) = GAME.from(TOKENIZER, WHITESPACES).parse(input)
}

fun sumIdsIfIsPossible(lines: Sequence<String>, limits: Round = Round(12, 13, 14)) =
    lines.map { CubeGameParser.parse(it) }
        .filter { it.isPossible(limits) }
        .sumOf { it.id }
