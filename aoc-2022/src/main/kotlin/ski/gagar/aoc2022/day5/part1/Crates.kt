package ski.gagar.aoc2022.day5.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day4.part1.countFullyIntersecting
import java.util.Deque
import java.util.ArrayDeque
import java.util.StringJoiner

data class Crate(val name: String) {
    init {
        require(name.length == 1)
    }
}

class CrateStock(stacks: Iterable<Iterable<Crate>>) {
    private val stacksW: MutableList<ArrayDeque<Crate>> = mutableListOf()

    init {
        for (stack in stacks) {
            val newStack = ArrayDeque<Crate>()

            for (crate in stack) {
                newStack.addLast(crate)
            }
            this.stacksW.add(newStack)
        }
    }

    val stacks: List<List<Crate>>
        get() = this.stacksW.map { it.toList() }

    fun move(amount: Int, from: Int, to: Int) {
        val fromStack = stacksW[from]
        val toStack = stacksW[to]

        for (i in 0 until amount) {
            val crate = fromStack.removeLast()
            toStack.addLast(crate)
        }
    }
}

data class CrateMoveInstruction(val amount: Int, val from: Int, val to: Int)

fun CrateMoveInstruction.apply(stock: CrateStock) {
    stock.move(amount, from, to)
}


private val CRATE_RE = """\[([A-Z])]""".toRegex()
private val NUMBERS_RE = """[\s0-9]+""".toRegex()

fun parseCrates(lines: Iterator<String>): List<Deque<Crate>> {
    val stacks = mutableListOf<ArrayDeque<Crate>>()

    fun expandStacks(toSize: Int) {
        for (i in stacks.size until toSize) {
            stacks.add(ArrayDeque())
        }
    }

    for (line in lines) {
        if (line.isEmpty())
            break

        if (line.matches(NUMBERS_RE))
            continue

        val chunks = line.chunked(4).map { it.trim() }

        for ((ix, chunk) in chunks.withIndex()) {
            if (chunk.isBlank()) {
                continue
            }
            val match = CRATE_RE.matchEntire(chunk)
            require(match != null)
            expandStacks(ix + 1)
            stacks[ix].addFirst(Crate(match.groups[1]!!.value))
        }
    }
    return stacks
}

object CrateMoveInstructionParser {
    private val POSITIVE_NUMBER = Terminals.DecimalLiteral.TOKENIZER.map {
        it.text().toInt().also { res -> check(res >= 0) }
    }

    private val MOVE = Parsers.sequence(
        Scanners.string("move"),
        Scanners.WHITESPACES,
        POSITIVE_NUMBER
    ) { _, _, num ->
        num
    }

    private val FROM = Parsers.sequence(
        Scanners.string("from"),
        Scanners.WHITESPACES,
        POSITIVE_NUMBER
    ) { _, _, num ->
        num
    }

    private val TO = Parsers.sequence(
        Scanners.string("to"),
        Scanners.WHITESPACES,
        POSITIVE_NUMBER
    ) { _, _, num ->
        num
    }

    private val INSTRUCTION = Parsers.sequence(
        MOVE,
        Scanners.WHITESPACES,
        FROM,
        Scanners.WHITESPACES,
        TO
    ) { amount, _, from1, _, to1 ->
        CrateMoveInstruction(amount, from1 - 1, to1 - 1)
    }


    fun parse(str: String) = INSTRUCTION.parse(str)
}

fun doMoves(lines: Sequence<String>): String {
    val itr = lines.iterator()
    val stock = CrateStock(parseCrates(itr))

    for (line in itr) {
        CrateMoveInstructionParser.parse(line).apply(stock)
    }

    return buildString {
        for (stack in stock.stacks) {
            append(stack.lastOrNull()?.name ?: " ")
        }
    }

}
