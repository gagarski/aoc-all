package ski.gagar.aoc2023.day15.part2

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc2023.day15.part1.lavaHash
import ski.gagar.aoc2023.day4.part1.ScratchCardParser

sealed interface LensOp

data class RemoveLens(val name: String) : LensOp
data class InsertLens(val name: String, val focalLength: Int) : LensOp

class LensBox {
    private val lenses_ = linkedMapOf<String, Int>()

    val lenses: Map<String, Int>
        get() = lenses_

    fun insert(name: String, focalLength: Int) {
        lenses_[name] = focalLength
    }

    fun remove(name: String) {
        lenses_.remove(name)
    }

    fun isEmpty() = lenses_.isEmpty()
}

class LensBoxes {
    private val boxes_ = linkedMapOf<Int, LensBox>()

    val boxes: Map<Int, LensBox>
        get() = boxes_

    fun insert(name: String, focalLength: Int) {
        val box = boxes_[name.lavaHash()] ?: LensBox()
        box.insert(name, focalLength)
        boxes_[name.lavaHash()] = box
    }

    fun remove(name: String) {
        val hash = name.lavaHash()
        val box = boxes_[hash] ?: return
        box.remove(name)
        if (box.isEmpty()) {
            boxes_.remove(hash)
        }
    }
}

fun LensBoxes.applyOp(op: LensOp) = when (op) {
    is RemoveLens -> remove(op.name)
    is InsertLens -> insert(op.name, op.focalLength)
}


fun LensBoxes.sumPowers() =
    boxes.entries.asSequence().sumOf { (boxN, box) ->
        box.lenses.values.withIndex().sumOf {
            (lensN, focusValue) -> (boxN.toLong() + 1) * (lensN.toLong() + 1) * focusValue.toLong()
        }
    }

object LensOpParser {
    private val COMMA = ","
    private val EQUALS = "="
    private val MINUS = "-"

    private val TERMINALS =
        Terminals.operators(COMMA, EQUALS, MINUS)
            .words(Scanners.IDENTIFIER)
            .build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )

    private val LENS_NAME = Terminals.Identifier.PARSER
    private val FOCAL_DISTANCE = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val INSERT_LENS_CMD = Parsers.sequence(
        LENS_NAME,
        TERMINALS.token(EQUALS),
        FOCAL_DISTANCE
    ) { name, _, focalDistance ->
        InsertLens(name, focalDistance)
    }

    private val REMOVE_LENS_CMD = Parsers.sequence(
        LENS_NAME,
        TERMINALS.token(MINUS),
    ) { name, _ ->
        RemoveLens(name)
    }

    private val CMD = Parsers.or(INSERT_LENS_CMD, REMOVE_LENS_CMD)

    private val CMDS = CMD.sepBy(TERMINALS.token(COMMA))

    fun parse(desc: String) = CMDS.from(TOKENIZER, Scanners.WHITESPACES.skipMany()).parse(desc)

}

fun sumPowers(input: String): Long {
    val boxes = LensBoxes()

    for (op in LensOpParser.parse(input)) {
        boxes.applyOp(op)
    }

    return boxes.sumPowers()
}