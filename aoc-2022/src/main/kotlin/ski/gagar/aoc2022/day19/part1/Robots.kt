package ski.gagar.aoc2022.day19.part1

import kotlinx.collections.immutable.*
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream

enum class Currency {
    ORE, CLAY, OBSIDIAN, GEODE;
}

@JvmInline
value class Price(val amounts: Map<Currency, Int>) {
    init {
        require(amounts.all { (_, v) -> v > 0 })
    }

    companion object {
        val FREE = Price(mapOf())
    }
}



class Wallet(
    val amounts: PersistentMap<Currency, Int>
) {
    init {
        require(amounts.all { (_, v) -> v >= 0 })
    }

    fun deposit(amount: Int, currency: Currency): Wallet {
        require(amount > 0)
        return Wallet(amounts.put(currency, (amounts[currency] ?: 0) + amount))
    }

    fun deposit(price: Price): Wallet {
        val patch = price.amounts.mapValues { (k, v) -> (amounts[k] ?: 0) + v }
        return Wallet(amounts.putAll(patch))
    }

    fun canAfford(amount: Int, currency: Currency): Boolean {
        require(amount > 0)
        return amount <= (amounts[currency] ?: 0)
    }

    fun canAfford(price: Price): Boolean = price.amounts.all { (k, v) -> canAfford(v, k) }

    fun withdraw(amount: Int, currency: Currency): Wallet {
        require(amount > 0)
        var res = amounts[currency] ?: 0

        require(res >= amount)

        res -= amount

        return Wallet(amounts.put(currency, res))
    }

    fun withdraw(price: Price): Wallet {
        val patch = price.amounts.mapValues { (k, v) -> (amounts[k] ?: 0) - v }
        require(patch.values.all { it >= 0 })

        return Wallet(amounts.putAll(patch))
    }

    override fun toString(): String {
        return "Wallet(amounts=$amounts)"
    }


    companion object {
        val EMPTY = Wallet(persistentMapOf())
    }
}

data class Robot(val produces: Currency, val price: Price)

data class Blueprint(val id: Int, val robots: Map<Currency, Robot>) {
    init {
        require(robots.size == Currency.values().size)
    }
}

sealed interface Move

data class DoNothing(val affordable: Set<Currency>) : Move
data class Purchase(val robotProducingCurrency: Currency) : Move

data class CourseOfAction(val moves: List<Move>, val result: Wallet)

data class State(
    val moves: PersistentList<Move>,
    val robotsPurchased: PersistentMap<Robot, Int>,
    val wallet: Wallet,
    val stepsLeft: Int,
    val targetCurrency: Currency,
    val blueprint: Blueprint,
) {

    private val priceIncreasePerMove: Map<Currency, Int> by lazy {
        val res = mutableMapOf<Currency, Int>()
        for ((k, v) in robotsPurchased) {
            res[k.produces] = (res[k.produces] ?: 0) + v
        }
        res
    }

    private fun Robot.canBeReached(): Boolean {
        val increase = priceIncreasePerMove
        return price.amounts.all { (k, v) ->
            (wallet.amounts[k] ?: 0) + (increase[k] ?: 0) * (stepsLeft) >= v
        }
    }

    fun nextMoves() = sequence {
        if (stepsLeft == 0)
            return@sequence

        val targetRobot = blueprint.robots[targetCurrency]!!

        if (wallet.canAfford(targetRobot.price)) {
            yield(Purchase(targetRobot.produces))
            return@sequence
        }

        val newAffordable = (moves.lastOrNull() as? DoNothing)?.affordable?.toMutableSet() ?: mutableSetOf()

        val toIter = blueprint.robots.filter { (k, v) ->
            v.produces != targetCurrency && wallet.canAfford(v.price)  && k !in newAffordable
        }

        for ((k, _) in toIter) {
            yield(Purchase(k))
        }
        newAffordable.addAll(toIter.keys)

        val nonAffordable = blueprint.robots.filter { (k, v) ->
            !wallet.canAfford(v.price)
        }

        if (targetRobot.canBeReached() || nonAffordable.values.any { it.canBeReached() }) {
            yield(DoNothing(newAffordable))
        }

    }
}

fun State.maxTheoreticalGain(targetCurrency: Currency) =
    (1 until stepsLeft).sum() + (wallet.amounts[targetCurrency] ?: 0) +
            (robotsPurchased[blueprint.robots[targetCurrency]] ?: 0 ) * stepsLeft

data class NextMoveAndState(val nextMove: Move, val state: State) {
    fun apply() = State(
        moves = state.moves.add(nextMove),
        robotsPurchased = when (nextMove) {
            is DoNothing -> state.robotsPurchased
            is Purchase -> state.robotsPurchased.put(state.blueprint.robots[nextMove.robotProducingCurrency]!!, (state.robotsPurchased[state.blueprint.robots[nextMove.robotProducingCurrency]!!] ?: 0) + 1)
        },
        wallet = state.robotsPurchased.asSequence().fold(
            initial = when (nextMove) {
                is DoNothing -> state.wallet
                is Purchase -> state.wallet.withdraw(state.blueprint.robots[nextMove.robotProducingCurrency]!!.price)
            }
        ) { wallet, (robot, amount) ->
          wallet.deposit(amount, robot.produces)
        },
        stepsLeft = state.stepsLeft - 1,
        targetCurrency = state.targetCurrency,
        blueprint = state.blueprint
    )
}

fun Blueprint.bestCourseOfAction(
    limit: Int = 24,
    initialRobots: Map<Robot, Int> = mapOf(Robot(Currency.ORE, Price.FREE) to 1),
    targetCurrency: Currency = Currency.GEODE
): CourseOfAction {
    val stack = ArrayDeque<NextMoveAndState>()
    val startState = State(
        moves = persistentListOf(),
        robotsPurchased = initialRobots.toPersistentMap(),
        wallet = Wallet.EMPTY,
        stepsLeft = limit,
        targetCurrency = targetCurrency,
        blueprint = this
    )

    for (move in startState.nextMoves()) {
        stack.addLast(NextMoveAndState(move, startState))
    }

    var bestStateSoFar: State? = null

    while (stack.isNotEmpty()) {
        val move = stack.removeLast()

        val nextState = move.apply()

        if (nextState.maxTheoreticalGain(targetCurrency) <= (bestStateSoFar?.wallet?.amounts?.get(targetCurrency) ?: -1))
            continue

        var added = false

        for (nextMove in nextState.nextMoves()) {
            stack.addLast(NextMoveAndState(nextMove, nextState))
            added = true
        }

        if (!added) {
            val currentTarget = bestStateSoFar?.wallet?.amounts?.get(targetCurrency) ?: 0
            val nextTarget = nextState.wallet.amounts[targetCurrency] ?: 0

            if (nextTarget >= currentTarget) {
                bestStateSoFar = nextState
            }
        }
    }

    return CourseOfAction(bestStateSoFar!!.moves, bestStateSoFar.wallet)
}

fun List<Blueprint>.quality(
    limit: Int = 24,
    initialRobots: Map<Robot, Int> = mapOf(Robot(Currency.ORE, Price.FREE) to 1),
    targetCurrency: Currency = Currency.GEODE
): Int {
    var q = 0

    for (bp in this) {
        q += bp.id * (bp.bestCourseOfAction(limit, initialRobots, targetCurrency).result.amounts[targetCurrency] ?: 0)
    }

    return q
}

object RobotsParser {
    private const val CAPITAL_BLUEPRINT = "Blueprint"
    private const val CAPITAL_EACH = "Each"
    private const val ROBOT = "robot"
    private const val COSTS = "costs"
    private const val AND = "and"

    private const val ORE = "ore"
    private const val CLAY = "clay"
    private const val OBSIDIAN = "obsidian"
    private const val GEODE = "geode"

    private const val FULL_STOP = "."
    private const val COLON = ":"
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TERMINALS =
        Terminals.operators(
            FULL_STOP,
            COLON,
            NL,
            NL_WIN
        )
            .words(Scanners.IDENTIFIER)
            .keywords(
                CAPITAL_BLUEPRINT,
                CAPITAL_EACH,
                ROBOT,
                COSTS,
                AND,
                ORE,
                CLAY,
                OBSIDIAN,
                GEODE
            ).build()

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val BLUEPRINT_ID = Parsers.sequence(
        TERMINALS.token(CAPITAL_BLUEPRINT),
        INTEGER
    ) { _, id -> id }

    private val ORE_PARSER = TERMINALS.token(ORE).map { Currency.ORE }
    private val CLAY_PARSER = TERMINALS.token(CLAY).map { Currency.CLAY }
    private val OBSIDIAN_PARSER = TERMINALS.token(OBSIDIAN).map { Currency.OBSIDIAN }
    private val GEODE_PARSER = TERMINALS.token(GEODE).map { Currency.GEODE }

    private val CURRENCY = Parsers.or(
        ORE_PARSER, CLAY_PARSER, OBSIDIAN_PARSER, GEODE_PARSER
    )

    private val CURRENCY_WITH_AMOUNT = Parsers.sequence(
        INTEGER,
        CURRENCY
    ) { amount, currency ->
        currency to amount
    }

    private val PRICE =
        CURRENCY_WITH_AMOUNT.sepBy(TERMINALS.token(AND)).map { Price(it.toMap()) }

    private val ROBOT_COST = Parsers.sequence(
        TERMINALS.token(CAPITAL_EACH),
        CURRENCY,
        TERMINALS.phrase(ROBOT, COSTS),
        PRICE,
        TERMINALS.token(FULL_STOP)
    ) { _, ccy, _, price, _ ->
        Robot(ccy, price)
    }

    private val ROBOT_COSTS = ROBOT_COST.many()

    private val BLUEPRINT = Parsers.sequence(
        BLUEPRINT_ID,
        TERMINALS.token(COLON),
        ROBOT_COSTS
    ) { id, _, costs ->
        Blueprint(id, costs.associateBy { it.produces })
    }

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val BLUEPRINTS = Parsers.sequence(
        NEWLINE.many(),
        BLUEPRINT.sepBy(NEWLINE.many1()),
        NEWLINE.many()
    ) { _, bps, _ -> bps }

    fun parse(input: String) = BLUEPRINTS.from(TOKENIZER, WHITESPACES).parse(input)

}

fun Blueprint.run(
    actions: List<Move>, initialRobots: Map<Robot, Int> = mapOf(Robot(Currency.ORE, Price.FREE) to 1),
): State {
    var state = State(
        moves = persistentListOf(),
        robotsPurchased = initialRobots.toPersistentMap(),
        wallet = Wallet.EMPTY,
        stepsLeft = actions.size,
        targetCurrency = Currency.GEODE,
        blueprint = this
    )

    for (action in actions) {
        state = NextMoveAndState(action, state).apply()
    }
    return state
}
