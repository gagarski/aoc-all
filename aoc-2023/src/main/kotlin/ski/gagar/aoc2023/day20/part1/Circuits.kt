package ski.gagar.aoc2023.day20.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc2023.day19.part1.PipelinesParser

sealed interface Gate {
    val name: String
    fun pulse(source: String, signal: Boolean): Boolean?
    fun reset() {}
}

class FlipFlop(override val name: String): Gate {
    private var mem: Boolean = false

    override fun pulse(source: String, signal: Boolean): Boolean? {
        if (signal) return null
        mem = !mem
        return mem
    }

    override fun reset() {
        mem = false
    }

}

class Conjunctor(override val name: String, inputs: Collection<String>): Gate {
    private val mem: MutableMap<String, Boolean> = inputs.associateWith { false }.toMutableMap()

    val inputs: Set<String>
        get() = mem.keys

    override fun pulse(source: String, signal: Boolean): Boolean {
        require(source in mem)
        mem[source] = signal
        return !mem.values.all { it }
    }

    override fun reset() {
        for ((k, _) in mem) {
            mem[k] = false
        }
    }
}

class Junction(override val name: String): Gate {
    override fun pulse(source: String, signal: Boolean): Boolean = signal
}

enum class GateType {
    FLIP_FLOP {
        override val mnemonic: String = "%"
    }, CONJUNCTOR {
        override val mnemonic: String = "&"
    }, JUNCTION {
        override val mnemonic: String = ""
    };

    abstract val mnemonic: String
}

data class Wiring(val type: GateType, val name: String, val outputs: Set<String>) {
    val mnemonic: String
        get() = "${type.mnemonic}$name"
}

data class PulseCounter(val positive: Int = 0, val negative: Int = 0) {
    operator fun plus(other: PulseCounter) = PulseCounter(
        this.positive + other.positive, this.negative + other.negative
    )
}

data class Pulse(val source: String = "button",
                 val destination: String = "broadcaster",
                 val polarity: Boolean = false)

class Circuit(wirings: Set<Wiring>) {
    val wires =
        wirings.associateBy { it.name }.mapValues { (k, v) -> v.outputs }
    val gates: Map<String, Gate>

    init {
        val inputs = wirings.asSequence().flatMap {w ->
            w.outputs.map { o -> o to w.name }
        }.groupBy({ it.first }, { it.second })

        gates = wirings.asSequence().map {
            when (it.type) {
                GateType.FLIP_FLOP -> FlipFlop(it.name)
                GateType.JUNCTION -> Junction(it.name)
                GateType.CONJUNCTOR -> Conjunctor(it.name, inputs[it.name] ?: setOf())
            }
        }.associateBy { it.name }
        println("OOO")
    }

    fun simulate(initPulse: Pulse = Pulse()): PulseCounter {
        var pos = 0
        var neg = 0

        val queue = ArrayDeque<Pulse>()
        queue.add(initPulse)

        while (queue.isNotEmpty()) {
            val processed = queue.removeFirst()
            if (processed.polarity) {
                pos++
            } else {
                neg++
            }

            val elem = gates[processed.destination]
                ?: continue
            val pulse = elem.pulse(processed.source, processed.polarity) ?: continue

            val newSource = processed.destination

            for (newDest in wires[newSource] ?: setOf()) {
                queue.add(Pulse(newSource, newDest, pulse))
            }
        }

        return PulseCounter(pos, neg)
    }

    fun reset() {
        for (gate in gates.values) {
            gate.reset()
        }
    }
}

object CircuitsParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private const val ARROW = "->"
    private const val COMMA = ","
    private const val PERCENT = "%"
    private const val AMPERSAND = "&"

    private val TERMINALS = Terminals.operators(
        NL, NL_WIN, ARROW, COMMA, PERCENT, AMPERSAND
    )
        .words(Scanners.IDENTIFIER)
        .build()

    private val TOKENIZER = TERMINALS.tokenizer()

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val GATE_NAME = Terminals.identifier()

    private val JUNCTION_GATE = GATE_NAME.map { Gate(GateType.JUNCTION, it) }
    private val FLIP_FLOP_GATE = Parsers.sequence(
        TERMINALS.token(PERCENT),
        GATE_NAME
    ) { _, name ->
        Gate(GateType.FLIP_FLOP, name)
    }
    private val CONJUNCTOR_GATE = Parsers.sequence(
        TERMINALS.token(AMPERSAND),
        GATE_NAME
    ) { _, name ->
        Gate(GateType.CONJUNCTOR, name)
    }

    private val GATE = Parsers.or(JUNCTION_GATE, FLIP_FLOP_GATE, CONJUNCTOR_GATE)
    private val OUTPUTS = GATE_NAME.sepBy(TERMINALS.token(COMMA)).map { it.toSet() }

    private val WIRING = Parsers.sequence(
        GATE,
        TERMINALS.token(ARROW),
        OUTPUTS
    ) { gate, _, outputs ->
        gate.withOutputs(outputs)
    }

    private val WIRINGS = WIRING.sepBy(NEWLINE.many1()).map { it.toSet() }

    private val WIRINGS_WITH_NL = Parsers.sequence(
        NEWLINE.many(),
        WIRINGS,
        NEWLINE.many()
    ) { _, w, _ ->
        w
    }

    fun parse(input: String) = WIRINGS_WITH_NL.from(TOKENIZER, WHITESPACES).parse(input)

    private data class Gate(val type: GateType, val name: String) {
        fun withOutputs(outputs: Set<String>) = Wiring(this.type, this.name, outputs)
    }

}

fun countSignals(input: String, n: Int = 1000): Int {
    val circuit = Circuit(CircuitsParser.parse(input))

    var ctr = PulseCounter()

    for (i in 0 until n) {
        ctr += circuit.simulate()
    }

    return ctr.positive * ctr.negative
}