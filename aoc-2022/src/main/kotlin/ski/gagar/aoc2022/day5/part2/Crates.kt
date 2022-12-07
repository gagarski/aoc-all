package ski.gagar.aoc2022.day5.part2

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day5.part1.Crate
import ski.gagar.aoc2022.day5.part1.CrateMoveInstruction
import ski.gagar.aoc2022.day5.part1.CrateMoveInstructionParser
import ski.gagar.aoc2022.day5.part1.parseCrates
import java.util.*

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

        val buf = ArrayDeque<Crate>()

        for (i in 0 until amount) {
            val crate = fromStack.removeLast()
            buf.addLast(crate)
        }

        for (i in 0 until amount) {
            val crate = buf.removeLast()
            toStack.addLast(crate)
        }
    }
}

fun CrateMoveInstruction.apply(stock: CrateStock) {
    stock.move(amount, from, to)
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

fun day5Part2() {
    println("day5/part2/crates: ${
        doMoves(getResourceAsStream("/ski.gagar.aoc.aoc2022.day5/crates.txt").bufferedReader().lineSequence())
    }")
}
