package ski.gagar.aoc2022.day22.part1

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import ski.gagar.aoc.util.getResourceAsStream
import java.util.BitSet

data class FieldRow(val offset: Int, val size: Int, val contents: BitSet)

class Field(private val rows: List<FieldRow>, private val instructions: List<Instruction>) {

}

enum class Direction {
    UP {
        override val cw: Direction
            get() = LEFT
        override val ccw: Direction
            get() = RIGHT
    },
    DOWN {
        override val cw: Direction
            get() = RIGHT
        override val ccw: Direction
            get() = LEFT
    },
    LEFT {
        override val cw: Direction
            get() = DOWN
        override val ccw: Direction
            get() = UP
    },
    RIGHT {
        override val cw: Direction
            get() = UP
        override val ccw: Direction
            get() = DOWN
    };

    abstract val cw: Direction
    abstract val ccw: Direction
}

sealed interface Instruction

data class Go(val amount: Int) : Instruction

enum class Rotate : Instruction {
    CW, CCW
}

object MonkeyFieldParser {
    private const val SPACE = ' '
    private const val DOT = '.'
    private const val HASH = '#'
    private const val NL = '\n'
    private const val NL_WIN = "\r\n"
    private const val L = 'L'
    private const val R = 'R'

    private val NEWLINE = Parsers.or(
        Scanners.isChar(NL),
        Scanners.string(NL_WIN)
    )

    private val SPACES = Scanners.isChar(SPACE).many().map { it.size }

    private val DOT_PARSER = Scanners.isChar(DOT).map { false }
    private val HASH_PARSER = Scanners.isChar(HASH).map { true }

    private val ROW_CONTENTS = Parsers.or(
        DOT_PARSER, HASH_PARSER
    ).many1().map {
        val res = BitSet()
        for ((ix, bool) in it.withIndex()) {
            res.set(ix, bool)
        }
        res to it.size
    }

    private val ROW = Parsers.sequence(
        SPACES,
        ROW_CONTENTS,
    ) { offset, (content, length) ->
        FieldRow(offset, length, content)
    }
    private val ROWS = ROW.sepBy(
        NEWLINE
    )

    private val L_PARSER = Scanners.isChar(L).map { Rotate.CCW }
    private val R_PARSER = Scanners.isChar(R).map { Rotate.CW }
    private val GO_PARSER = Scanners.INTEGER.map { Go(it.toInt()) }
    private val COMMAND = Parsers.or(
        L_PARSER,
        R_PARSER,
        GO_PARSER
    )
    private val COMMANDS = COMMAND.many()

    private val FIELD = Parsers.sequence(
        ROWS,
        NEWLINE,
        NEWLINE,
        COMMANDS
    ) { rows, _, _, cmds ->
        Field(rows, cmds)
    }

    fun parse(input: String) = FIELD.parse(input)
}

fun main() {
    println(MonkeyFieldParser.parse(getResourceAsStream("/ski.gagar.aoc.aoc2022.day22/field.txt").bufferedReader().readText()))
}