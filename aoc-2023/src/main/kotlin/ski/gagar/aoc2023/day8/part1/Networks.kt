package ski.gagar.aoc2023.day8.part1

import org.jparsec.*
import org.jparsec.Tokens.*
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns
import ski.gagar.aoc.util.getResourceAsStream
import java.util.EnumMap

enum class Direction {
    LEFT,
    RIGHT;

    companion object {
        val L_CHAR = 'L'
        val R_CHAR = 'R'
        fun from(char: Char) = when (char) {
            R_CHAR -> RIGHT
            L_CHAR -> LEFT
            else -> throw IllegalArgumentException("Wrong direction")
        }
    }
}

class NetworkMap(
    val instructions: List<Direction>,
    val map: Map<String, Map<Direction, String>>
) {
    fun followLen(source: String, dest: String): Int {
        var current = source
        var steps = 0
        outer@ while (true) {
            for (instr in instructions) {
                if (current == dest) break@outer
                val dests = map[current] ?: throw IllegalStateException("Node $current not found")
                current = dests[instr] ?: throw IllegalStateException("Cannot follow instruction $instr from $current")
                steps++
            }
        }
        return steps
    }
}

object NetworkMapParser {
    object Identifier {
        val PARSER: Parser<String> = Terminals.fragment(Tag.IDENTIFIER)
        val TOKENIZER: Parser<Fragment> = Patterns.isChar(CharPredicates.IS_ALPHA_NUMERIC_)
            .many()
            .toScanner("word")
            .source().map {
                fragment(it, Tag.IDENTIFIER)
            }
    }
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val COMMA = ","
    private val L_PAREN = "("
    private val R_PAREN = ")"
    private val ASSIGN = "="

    private val OPERATORS = Terminals.operators(COMMA, L_PAREN, R_PAREN, ASSIGN, NL, NL_WIN)

    private val WORD = Patterns.isChar(CharPredicates.IS_ALPHA_NUMERIC_).many1()
        .toScanner("word").source();

    private val TOKENIZER = Parsers.or(OPERATORS.tokenizer(), Identifier.TOKENIZER)

    private val INSTRUCTIONS = Identifier.PARSER.map { it.map { c -> Direction.from(c) } }

    private val NODE_NAME = Identifier.PARSER

    private val DESTINATIONS = Parsers.sequence(
        OPERATORS.token(L_PAREN),
        NODE_NAME,
        OPERATORS.token(COMMA),
        NODE_NAME,
        OPERATORS.token(R_PAREN)
    ) { _, left, _, right, _ ->
        val map = EnumMap<Direction, String>(Direction::class.java)
        map[Direction.LEFT] = left
        map[Direction.RIGHT] = right
        map
    }

    private val NODE = Parsers.sequence(
        NODE_NAME,
        OPERATORS.token(ASSIGN),
        DESTINATIONS
    ) { source, _, destinations ->
        source to destinations
    }
    private val NEWLINE = Parsers.or(
        OPERATORS.token(NL),
        OPERATORS.token(NL_WIN)
    )
    private val NODES = NODE.sepBy(
        NEWLINE.many1()
    ).map {
        it.toMap()
    }

    private val MAP_WITH_INSTRUCTIONS = Parsers.sequence(
        INSTRUCTIONS,
        NEWLINE.many1(),
        NODES,
        NEWLINE.many()
    ) { instr, _, nodes, _ ->
        NetworkMap(instr, nodes)
    }

    private val PARSER = MAP_WITH_INSTRUCTIONS.from(TOKENIZER, WHITESPACES)

    fun parse(string: String) = PARSER.parse(string)
}

fun nSteps(input: String, source: String = "AAA", dest: String = "ZZZ") =
    NetworkMapParser.parse(input).followLen(source, dest)
