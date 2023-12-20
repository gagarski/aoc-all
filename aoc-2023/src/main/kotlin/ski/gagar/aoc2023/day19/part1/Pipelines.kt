package ski.gagar.aoc2023.day19.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import java.util.*

enum class Trait(val str: String) {
    X("x"),
    M("m"),
    A("a"),
    S("s");

    companion object {
        private val byStr = entries.associateBy { it.str }
        fun from(str: String) = byStr[str] ?: throw IllegalArgumentException("Unknown trait $str")
    }
}

class Item(traits: Map<Trait, Int>) {
    val traits: Map<Trait, Int>

    init {
        val toSet = EnumMap<Trait, Int>(Trait::class.java)
        for (trait in Trait.entries) {
            toSet[trait] = traits[trait] ?: 0
        }
        this.traits = toSet
    }

    operator fun get(trait: Trait) = traits[trait] ?: 0

    val x
        get() = get(Trait.X)
    val m
        get() = get(Trait.M)
    val a
        get() = get(Trait.A)
    val s
        get() = get(Trait.S)
}

sealed interface Result
sealed interface Action
data object Accept : Action, Result
data object Reject : Action, Result
data class JumpToPipeline(val name: String) : Action

sealed interface PipelineBranch {
    val dest: Action
    fun accept(item: Item) : Action?
}

data class Unconditional(override val dest: Action) : PipelineBranch {
    override fun accept(item: Item): Action = dest
}

sealed class Conditional(override val dest: Action) : PipelineBranch {
    abstract fun predicate(item: Item): Boolean
    override fun accept(item: Item): Action? = if (predicate(item)) dest else null
}

data class LtConditional(val trait: Trait, val value: Int, override val dest: Action) : Conditional(dest) {
    override fun predicate(item: Item): Boolean = item[trait] < value
}

data class GtConditional(val trait: Trait, val value: Int, override val dest: Action) : Conditional(dest) {
    override fun predicate(item: Item): Boolean = item[trait] > value
}

data class Pipeline(val name: String, val branches: List<PipelineBranch>) {
    fun accept(item: Item) =
        branches.firstNotNullOfOrNull { it.accept(item) }
            ?: throw IllegalStateException("No decision on $this for $item")
}

class Pipelines(pipelines: List<Pipeline>) {
    val pipelines = pipelines.associateBy { it.name }

    fun run(item: Item, start: String = "in"): Result {
        var pipeline = pipelines[start] ?: throw IllegalStateException("Pipeline $start not found")

        while (true) {
            when (val res = pipeline.accept(item)) {
                Accept -> return Accept
                Reject -> return Reject
                is JumpToPipeline -> {
                    pipeline = pipelines[res.name] ?: throw IllegalStateException("Pipeline ${res.name} not found")
                }
            }
        }
    }
}

class PipelinesAndItems(val pipelines: Pipelines, val items: List<Item>) {
    fun classify(startPipeline: String = "in"): Map<Result, List<Item>> {
        val res = mapOf<Result, MutableList<Item>>(
            Accept to mutableListOf(),
            Reject to mutableListOf()
        )

        for (item in items) {
            res[pipelines.run(item, startPipeline)]!!.add(item)
        }

        return res
    }
}

object PipelinesParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private const val L_BRACE = "{"
    private const val R_BRACE = "}"
    private const val COLON = ":"
    private const val LT = "<"
    private const val GT = ">"
    private const val EQ = "="
    private const val COMMA = ","
    private const val R = "R"
    private const val A = "A"

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER

    private val TERMINALS = Terminals.operators(
        L_BRACE,
        R_BRACE,
        COLON,
        LT,
        GT,
        EQ,
        COMMA,
        NL,
        NL_WIN
    )
        .words(Scanners.IDENTIFIER)
        .keywords(R, A)
        .build()

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { it.toInt() }
    private val TRAIT_NAME = Terminals.identifier()
    private val TRAIT = TRAIT_NAME.map { Trait.from(it) }
    private val TRAIT_AMOUNT = INTEGER

    private val TRAIT_ENTRY = Parsers.sequence(
        TRAIT,
        TERMINALS.token(EQ),
        TRAIT_AMOUNT
    ) { trait, _, amount ->
        trait to amount
    }

    private val ITEM = Parsers.sequence(
        TERMINALS.token(L_BRACE),
        TRAIT_ENTRY.sepBy(TERMINALS.token(COMMA)),
        TERMINALS.token(R_BRACE),
    ) { _, entries, _ ->
        Item(entries.toMap())
    }

    private val ITEMS = ITEM.sepBy(NEWLINE)

    private val PIPELINE_NAME = Terminals.identifier()

    private val REJECT = TERMINALS.token(R).map { Reject }
    private val ACCEPT = TERMINALS.token(A).map { Accept }
    private val JUMP_TO_PIPELINE = Terminals.identifier().map { JumpToPipeline(it) }
    private val ACTION = Parsers.or(
        REJECT, ACCEPT, JUMP_TO_PIPELINE
    )
    private val UNCONDITIONAL = ACTION.map { Unconditional(it) }
    private val LT_CONDITIONAL = Parsers.sequence(
        TRAIT_NAME,
        TERMINALS.token(LT),
        TRAIT_AMOUNT,
        TERMINALS.token(COLON),
        ACTION
    ) { trait, _, amount, _, action ->
        LtConditional(Trait.from(trait), amount, action)
    }

    private val GT_CONDITIONAL = Parsers.sequence(
        TRAIT_NAME,
        TERMINALS.token(GT),
        TRAIT_AMOUNT,
        TERMINALS.token(COLON),
        ACTION
    ) { trait, _, amount, _, action ->
        GtConditional(Trait.from(trait), amount, action)
    }
    private val BRANCH = Parsers.or(
        LT_CONDITIONAL,
        GT_CONDITIONAL,
        UNCONDITIONAL,
    )
    private val PIPELINE = Parsers.sequence(
        PIPELINE_NAME,
        TERMINALS.token(L_BRACE),
        BRANCH.sepBy(TERMINALS.token(COMMA)),
        TERMINALS.token(R_BRACE),
    ) { name, _, branches, _ ->
        Pipeline(name, branches)
    }
    private val PIPELINES = PIPELINE.sepBy(NEWLINE).map { Pipelines(it) }

    private val PIPELINES_AND_ITEMS = Parsers.sequence(
        PIPELINES,
        NEWLINE,
        NEWLINE.many1(),
        ITEMS,
        NEWLINE.many()
    ) { pipelines, _, _, items, _ ->
        PipelinesAndItems(pipelines, items)
    }

    fun parse(input: String) = PIPELINES_AND_ITEMS.from(TOKENIZER, WHITESPACES).parse(input)
}

fun sumTraits(input: String) =
    PipelinesParser.parse(input).classify()[Accept]!!.sumOf {
        it.x + it.m + it.a + it.s
    }
