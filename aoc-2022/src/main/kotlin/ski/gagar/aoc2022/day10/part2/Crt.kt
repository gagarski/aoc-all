package ski.gagar.aoc2022.day10.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day10.part1.countStrength
import java.util.BitSet

class CathodeRayTube(commands: Sequence<Command>) {
    var x: Int = 1
        private set
    var ctr: Int = 0
        private set

    private var cyclesLeft: Int = -1
    private var nextCmd: Command? = null
    private val cmdItr = commands.iterator()

    init {
        fetchCmd()
    }


    private fun fetchCmd() {
        nextCmd = if (cmdItr.hasNext()) cmdItr.next() else null
        cyclesLeft = nextCmd?.cycles ?: Int.MAX_VALUE
    }

    private fun execCmd() {
        require(null != nextCmd)
        ctr += cyclesLeft
        cyclesLeft = 0
        nextCmd?.run(this)
    }

    fun hasCommandsToRun() = nextCmd != null

    fun runCommand() {
        execCmd()
        fetchCmd()
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

class Display(val width: Int, val height: Int, val spriteWidth: Int) {
    private val pixels = BitSet(width * height)
    var spriteStart = 0
        private
    var pixelDrawn = 0

    fun draw() {
        val pixelDrawnX = pixelDrawn % width
        if (pixelDrawnX in spriteStart until spriteStart + spriteWidth) {
            pixels.set(pixelDrawn, true)
        }
        pixelDrawn++
    }

    fun moveSprite(x: Int) {
        spriteStart = x - 1
    }

    override fun toString(): String = buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                append(if (pixels[width * y + x]) "#" else ".")
            }
            append("\n")
        }
    }
}

fun runLoopWise(lines: Sequence<String>, width: Int = 40, height: Int = 6, spriteWidth: Int = 3): Display {
    val display = Display(width, height, spriteWidth)
    val crt = CathodeRayTube(lines.map { CommandParser.parse(it) })

    while (crt.hasCommandsToRun()) {
        crt.run(1)
        display.moveSprite(crt.x)
        display.draw()
    }

    return display
}
