package ski.gagar.aoc2022.day10.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream

class CathodeRayTube(commands: Sequence<Command>) {
    var x: Int = 1
        private set
    var ctr: Int = 0
        private set

    private var cyclesLeft: Int = -1
    private lateinit var nextCmd: Command
    private val cmdItr = commands.iterator()

    init {
        fetchCmd()
    }


    private fun fetchCmd() {
        nextCmd = if (cmdItr.hasNext()) cmdItr.next() else NoOp
        cyclesLeft = nextCmd.cycles
    }

    private fun execCmd() {
        ctr += cyclesLeft
        cyclesLeft = 0
        nextCmd.run(this)
    }

    fun run(cycles: Int) {
        var left = cycles

        while (left > cyclesLeft) {
            left -= cyclesLeft
            execCmd()
            fetchCmd()
        }

        cyclesLeft -= left
        ctr += left
    }

    interface Command {
        val cycles: Int
        fun run(tube: CathodeRayTube)
    }

    object NoOp : Command {
        override val cycles: Int
            get() = 1

        override fun run(tube: CathodeRayTube) {
        }
    }



    data class AddX(val int: Int) : Command {
        override val cycles: Int = 2

        override fun run(tube: CathodeRayTube) {
            tube.x += int
        }
    }
}


object CommandParser {
    private val POSITIVE_NUMBER = Terminals.IntegerLiteral.TOKENIZER.map {
        it.text().toInt()
    }

    private val NEGATIVE_INTEGER = Parsers.sequence(
        Scanners.isChar('-'),
        Terminals.IntegerLiteral.TOKENIZER
    ) { _, int ->
        -int.text().toInt()
    }

    private val NUMBER = Parsers.or(
        POSITIVE_NUMBER,
        NEGATIVE_INTEGER
    )

    private val NOOP = Scanners.string("noop").map { CathodeRayTube.NoOp }

    private val ADDX = Parsers.sequence(
        Scanners.string("addx"),
        Scanners.WHITESPACES,
        NUMBER
    ) { _, _, num ->
        CathodeRayTube.AddX(num)
    }

    private val CMD = Parsers.or(NOOP, ADDX)

    fun parse(str: String) = CMD.parse(str)
}

fun countStrength(lines: Sequence<String>, period: Int = 40, phase: Int = 20, nPeriods: Int = 5): Int {
    val tube = CathodeRayTube(lines.map { CommandParser.parse(it) })
    var strength = 0
    tube.run(phase)
    strength += tube.ctr * tube.x

    for (i in 0 until nPeriods) {
        tube.run(period)
        strength += tube.ctr * tube.x
    }

    return strength
}
