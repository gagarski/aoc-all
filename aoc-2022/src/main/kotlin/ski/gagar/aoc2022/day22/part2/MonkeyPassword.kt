package ski.gagar.aoc2022.day22.part2

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day21.part1.ExpressionParser
import ski.gagar.aoc2022.day21.part1.MonkeyCalculator
import java.util.BitSet
import kotlin.math.absoluteValue
import kotlin.math.sign

data class FieldRow(val offset: Int, val size: Int, val contents: BitSet)

data class Coordinates(val x: Int, val y: Int)

class Field(private val rows: List<FieldRow>, private val instructions: List<Instruction>) {
    init {
        require(rows.isNotEmpty())
    }

    var myCoordinates = Coordinates(rows.first().offset, 0)
        private set
    var myDirection = Direction.RIGHT
        private set

    fun runInstructions() {
        for (instr in instructions) {
            when (instr) {
                is Go -> go(instr.amount)
                Rotate.CW -> myDirection = myDirection.cw
                Rotate.CCW -> myDirection = myDirection.ccw
            }
        }
    }

    private fun go(amount: Int) {
        when (myDirection) {
            Direction.UP -> goVertically(-amount)
            Direction.RIGHT -> goHorizontally(amount)
            Direction.DOWN -> goVertically(amount)
            Direction.LEFT -> goHorizontally(-amount)
        }
    }

    private fun goHorizontally(amount: Int) {
        val direction = amount.sign
        val amountAbs = amount.absoluteValue

        if (amountAbs == 0) return

        var x = myCoordinates.x
        val row = rows[myCoordinates.y]

        for (i in 0 until amountAbs) {
            val incremented = x + direction
            val nextX = when {
                incremented >= row.offset + row.size -> row.offset
                incremented < row.offset -> row.offset + row.size - 1
                else -> incremented
            }

            if (row.contents[nextX - row.offset])
                break

            x = nextX
        }

        myCoordinates = Coordinates(x, myCoordinates.y)
    }

    private fun goVertically(amount: Int) {
        val direction = amount.sign
        val amountAbs = amount.absoluteValue

        if (amountAbs == 0) return

        var y = myCoordinates.y

        for (i in 0 until amountAbs) {
            val nextY = nextY(y, direction)
            val row = rows[nextY]

            check(myCoordinates.x in row.offset until row.offset + row.size)

            if (row.contents[myCoordinates.x - row.offset])
                break
            y = nextY
        }

        myCoordinates = Coordinates(myCoordinates.x, y)
    }

    private fun nextY(y: Int, direction: Int): Int {
        require(direction.absoluteValue == 1)
        val initialY = y
        var currentY = y
        do {
            val incremented = currentY + direction
            currentY = when {
                incremented >= rows.size -> 0
                incremented < 0 -> rows.size - 1
                else -> incremented
            }

            if (currentY == initialY)
                break
            val row = rows[currentY]
        } while (myCoordinates.x !in row.offset until row.offset + row.size)

        return currentY
    }

}

enum class Direction {
    UP {
        override val cw: Direction
            get() = RIGHT
        override val ccw: Direction
            get() = LEFT
    },
    DOWN {
        override val cw: Direction
            get() = LEFT
        override val ccw: Direction
            get() = RIGHT
    },
    LEFT {
        override val cw: Direction
            get() = UP
        override val ccw: Direction
            get() = DOWN
    },
    RIGHT {
        override val cw: Direction
            get() = DOWN
        override val ccw: Direction
            get() = UP
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
        COMMANDS,
        NEWLINE.many()
    ) { rows, _, _, cmds, _ ->
        Field(rows, cmds)
    }

    fun parse(input: String) = FIELD.parse(input)
}

fun evaluate(input: String): Int {
    val field = MonkeyFieldParser.parse(input)
    field.runInstructions()
    return (field.myCoordinates.y + 1) * 1000 + (field.myCoordinates.x + 1) * 4 + when (field.myDirection) {
        Direction.RIGHT -> 0
        Direction.DOWN -> 1
        Direction.LEFT -> 2
        Direction.UP -> 3
    }
}


fun day22Part1() {
    println("day212part1/field: \n${
        evaluate(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day22/field.txt").bufferedReader().readText()
        )
    }")
}
