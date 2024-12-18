package ski.gagar.aoc2024.day17.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.parsers.sepOrSurroundedBy

class Processor(
    initA: Long = 0,
    initB: Long = 0,
    initC: Long = 0,
    private val program: List<Int>,
    private val printer: (Int) -> Unit,
) {
    init {
        require(program.all { it in 0..7 })
    }

    var a: Long = initA
        private set
    var b: Long = initB
        private set
    var c: Long = initC
        private set
    var pc: Int = 0
        private set

    fun step(): Boolean {
        if (pc >= program.size)
            return false
        if (pc == program.size - 1)
            error("Alignment error")
        val operation = Command.from(program[pc])
        operation.execute(this)
        pc += 2
        return true
    }

    fun run() {
        while (step()) {
            // do nothing else
        }
    }

    private enum class Command(val opCode: Int) {
        ADV(0) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.a = processor.a ushr operand.toInt()
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = comboOperand(processor, opWord)
        }, BXL(1) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.b = processor.b xor operand
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = literalOperand(opWord)
        }, BST(2) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.b = operand.mod8()
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = comboOperand(processor, opWord)

        }, JNZ(3) {
            override fun doExecute(processor: Processor, operand: Long) {
                if (processor.a != 0L) {
                    processor.pc = operand.toInt() - 2
                }
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = literalOperand(opWord)

        }, BXC(4) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.b = processor.b xor processor.c
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = noOperand(opWord)

        }, OUT(5) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.printer(operand.mod8().toInt())
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = comboOperand(processor, opWord)
        }, BDV(6) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.b = processor.a ushr operand.toInt()
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = comboOperand(processor, opWord)
        }, CDV(7) {
            override fun doExecute(processor: Processor, operand: Long) {
                processor.c = processor.a ushr operand.toInt()
            }

            override fun evaluateOperand(processor: Processor, opWord: Int): Long = comboOperand(processor, opWord)
        };

        init {
            require(opCode in 0..7)
        }
        abstract fun doExecute(processor: Processor, operand: Long)
        abstract fun evaluateOperand(processor: Processor, opWord: Int): Long

        protected fun literalOperand(opWord: Int): Long = opWord.toLong()
        protected fun comboOperand(processor: Processor, opWord: Int): Long =
            when (opWord) {
                in 0..3 -> literalOperand(opWord)
                4 -> processor.a
                5 -> processor.b
                6 -> processor.c
                else -> error("Unknown combo operand $opWord")
            }
        protected fun noOperand(opWord: Int): Long = 0


        fun execute(processor: Processor) {
            val opWord = processor.program[processor.pc + 1]
            val operand = evaluateOperand(processor, opWord)
            doExecute(processor, operand)
        }

        protected fun Long.mod8() = this % 8

        companion object {
            private val byOpCode = entries.associateBy { it.opCode }
            fun from(opCode: Int) = byOpCode[opCode] ?: error("Unknown opcode $opCode")
        }
    }
}

class CpuDescription {
    var program: List<Int>? = null
        private set

    private val registers_: MutableMap<String, Long> = mutableMapOf()

    fun check() {
        val p = program
        require(p != null) { "Program isn't initialized" }
        require(p.all { it in 0..7 }) { "Program must be in 0..7" }
        val r = registers_
        require(r != null) { "Registers must be initialized" }
        require(ALLOWED_REGISTERS.all { it in r }) { "All registers must be initialized" }
    }

    fun addProgram(program: List<Int>) {
        if (this.program != null)
            error("Program has already been initialized")
        this.program = program
    }

    val registers: Map<String, Long> = registers_

    private fun addRegister(name: String, value: Long) {
        if (name !in ALLOWED_REGISTERS) {
            error("$name is not an allowed register")
        }
        if (registers_[name] != null)
            error("Register with name $name already defined")
        registers_[name] = value
    }

    private fun processLine(line: Line) {
        when (line) {
            is RegisterLine -> addRegister(line.name, line.value)
            is ProgramLine -> addProgram(line.program)
        }
    }

    fun processLines(lines: List<Line>) {
        for (line in lines) {
            processLine(line)
        }
    }

    sealed interface Line
    data class RegisterLine(val name: String, val value: Long) : Line
    data class ProgramLine(val program: List<Int>) : Line

    companion object {
        val ALLOWED_REGISTERS = setOf("A", "B", "C")
    }
}

object CpuDescriptionParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private const val REGISTER = "Register"
    private const val COLON = ":"
    private const val COMMA = ","
    private const val PROGRAM = "Program"

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER

    private val TERMINALS = Terminals
        .operators(COLON, COMMA, NL, NL_WIN)
        .words(Scanners.IDENTIFIER)
        .keywords(REGISTER, PROGRAM)
        .build()

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER
    )

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )
    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toInt()
    }
    private val LONG = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toLong()
    }

    private val NAME = Terminals.Identifier.PARSER


    private val REGISTER_LINE = Parsers.sequence(
        TERMINALS.token(REGISTER), NAME, TERMINALS.token(COLON), LONG
    ) { _, name, _, value ->
        CpuDescription.RegisterLine(name, value)
    }

    private val LIST_OF_INT = INTEGER.sepBy(TERMINALS.token(COMMA))

    private val PROGRAM_LINE = Parsers.sequence(
        TERMINALS.token(PROGRAM), TERMINALS.token(COLON), LIST_OF_INT
    ) { _, _, p ->
        CpuDescription.ProgramLine(p)
    }

    private val LINE = Parsers.or(REGISTER_LINE, PROGRAM_LINE)

    private val LINES = LINE.sepOrSurroundedBy(NEWLINE.many1())

    fun parse(input: String): CpuDescription {
        val lines = LINES.from(TOKENIZER, WHITESPACES).parse(input)
        val desc = CpuDescription()
        desc.processLines(lines)
        desc.check()
        return desc
    }
}

fun cpuOutput(desc: String): String {
    val cpuD = CpuDescriptionParser.parse(desc)
    val output = mutableListOf<Int>()
    val cpu = Processor(
        cpuD.registers["A"]!!,
        cpuD.registers["B"]!!,
        cpuD.registers["C"]!!,
        cpuD.program!!,
    ) { output.add(it) }

    cpu.run()
    return output.joinToString(",")
}
