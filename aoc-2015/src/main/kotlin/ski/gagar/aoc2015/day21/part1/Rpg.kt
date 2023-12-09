package ski.gagar.aoc2015.day21.part1

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import ski.gagar.aoc.util.getResourceAsStream

const val DEFAULT_PLAYER_HIT_POINTS = 100
val DEFAULT_SUPPLY = listOf(
    Weapon(
        name = "Dagger",
        cost = 8,
        damage = 4
    ),
    Weapon(
        name = "Shortsword",
        cost = 10,
        damage = 5
    ),
    Weapon(
        name = "Warhammer",
        cost = 25,
        damage = 6
    ),
    Weapon(
        name = "Longsword",
        cost = 40,
        damage = 7
    ),
    Weapon(
        name = "Greataxe",
        cost = 74,
        damage = 8
    ),

    Armor(
        name = "Leather",
        cost = 13,
        armor = 1
    ),
    Armor(
        name = "Chainmail",
        cost = 31,
        armor = 2
    ),
    Armor(
        name = "Splintmail",
        cost = 53,
        armor = 3
    ),
    Armor(
        name = "Bandedmail",
        cost = 75,
        armor = 4
    ),
    Armor(
        name = "Platemail",
        cost = 102,
        armor = 5
    ),

    Ring(
        name = "Damage +1",
        cost = 25,
        damage = 1
    ),
    Ring(
        name = "Damage +2",
        cost = 50,
        damage = 2
    ),
    Ring(
        name = "Damage +3",
        cost = 100,
        damage = 3
    ),
    Ring(
        name = "Defence +1",
        cost = 20,
        armor = 1
    ),
    Ring(
        name = "Defence +2",
        cost = 40,
        armor = 2
    ),
    Ring(
        name = "Defence +3",
        cost = 80,
        armor = 3
    ),
)

val DEFAULT_LIMITS: Limits = mapOf(
    Weapon::class.java to 1..1,
    Armor::class.java to 0..1,
    Ring::class.java to 0..2
)

sealed interface Item {
    val name: String
    val cost: Int
    val damage: Int
    val armor: Int
}

data class Weapon(override val name: String, override val cost: Int, override val damage: Int) : Item {
    override val armor: Int = 0
}

data class Armor(override val name: String, override val cost: Int, override val armor: Int) : Item {
    override val damage: Int = 0
}

data class Ring(override val name: String,
                override val cost: Int,
                override val damage: Int = 0,
                override val armor: Int = 0) : Item

typealias Inventory = Set<Item>

typealias Limits = Map<Class<out Item>, IntRange>

class Store(supply: List<Item>) {
    private val supplyByType = supply.groupBy { it::class.java }

    private suspend fun SequenceScope<PersistentSet<Item>>.buildCombo(base: PersistentSet<Item>,
                                                                      supply: List<Item>,
                                                                      max: Int,
                                                                      startIndex: Int = 0) {
        yield(base)

        if (max > 0) {
            for ((ix, option) in supply.asSequence().drop(startIndex).withIndex()) {
                val realIndex = startIndex + ix
                if (option !in base) buildCombo(base.add(option), supply, max - 1, realIndex + 1)
            }
        }
    }

    private fun getSingleTypeCombos(
        allLimits: Limits,
        klass: Class<out Item>,
        inventory: PersistentSet<Item>
    ): Sequence<PersistentSet<Item>> {
        val supply = supplyByType[klass]!!
        val limits = allLimits[klass]!!
        val oldSize = inventory.size
        return sequence {
            buildCombo(inventory, supply, limits.last)
        }.filter { (it.size - oldSize) >= limits.first }
    }

    /**
     * Generates all possible inventories, given limits.
     *
     * We could go with more smart approach:
     *  - first brute-forcing (or calculating?) minimum player stats (or stats relationship) required to win
     *  - then trying to find best fit
     */
    fun getPossibleInventories(limits: Limits): Sequence<Inventory> {
        val currentInventories: Sequence<PersistentSet<Item>> = sequence {
            yield(persistentSetOf())
        }

        fun extendInventories(
            currentInventories: Sequence<PersistentSet<Item>>,
            classes: List<Class<out Item>>): Sequence<PersistentSet<Item>> {
            if (classes.isEmpty())
                return currentInventories

            val extended = sequence {
                for (inv in currentInventories) {
                    yieldAll(getSingleTypeCombos(limits, classes.first(), inv))
                }
            }

            return extendInventories(extended, classes.drop(1))
        }
        return extendInventories(currentInventories, limits.keys.toList())
    }
}

val Inventory.cost
    get() = sumOf { it.cost }
val Inventory.damage
    get() = sumOf { it.damage }
val Inventory.armor
    get() = sumOf { it.armor }


data class BattleResult(val win: Boolean, val rounds: Int)

data class Player(val hitPoints: Int, val damage: Int, val armor: Int) {
    constructor(hitPoints: Int, inventory: Inventory) :
            this(hitPoints, inventory.damage, inventory.armor)


    fun battle(other: Player): BattleResult {
        val damageToOtherPerTurn = maxOf(this.damage - other.armor, 0)
        val damageToThisPerTurn = maxOf(other.damage - this.armor, 0)

        val thisWillDieOn =
            if (damageToThisPerTurn == 0) {
                Int.MAX_VALUE
            } else {
                this.hitPoints / damageToThisPerTurn +
                        (if (this.hitPoints % damageToThisPerTurn == 0) 0 else 1)
            }
        val otherWillDieOn =
            if (damageToOtherPerTurn == 0) {
                Int.MAX_VALUE
            } else {
                other.hitPoints / damageToOtherPerTurn +
                        (if (other.hitPoints % damageToOtherPerTurn == 0) 0 else 1)
            }

        if (thisWillDieOn == Int.MAX_VALUE && otherWillDieOn == Int.MAX_VALUE) {
            return BattleResult(false, Int.MAX_VALUE)
        }

        return BattleResult(thisWillDieOn >= otherWillDieOn, minOf(thisWillDieOn, otherWillDieOn))
    }

    /**
     * Other approach for battle simulation, done for debugging purposes
     */
    fun battleSim(other: Player): BattleResult {
        var attack = this
        var attackHealth = this.hitPoints
        var defend = other
        var defendHealth = other.hitPoints
        var steps = 0
        while (true) {
            defendHealth -= (attack.damage - defend.armor)
            steps++

            if (defendHealth <= 0) {
                return BattleResult(
                    attack == this,
                    steps / 2 + (if (steps % 2 == 0) 0 else 1)
                )
            }
            val tmp = attack
            attack = defend
            defend = tmp
            val tmpH = attackHealth
            attackHealth = defendHealth
            defendHealth = tmpH
        }
    }

    companion object {
        private const val HIT_POINTS = "Hit Points"
        private const val DAMAGE = "Damage"
        private const val ARMOR = "Armor"
        private val SEPARATOR = """\s*:\s*""".toRegex()

        fun parse(string: String): Player {
            var hitPoints: Int? = null
            var damage: Int? = null
            var armor: Int? = null

            for (line in string.lineSequence()) {
                val parts = line.split(SEPARATOR)
                require(parts.size == 2)
                val char = parts[1].toInt()

                when (parts[0]) {
                    HIT_POINTS -> hitPoints = char
                    DAMAGE -> damage = char
                    ARMOR -> armor = char
                }
            }

            require(hitPoints != null)
            require(damage != null)
            require(armor != null)

            return Player(hitPoints, damage, armor)
        }
    }
}

data class BattleInfo(val hero: Player, val boss: Player, val inventory: Inventory, val res: BattleResult)

fun findMinWinCost(bossStats: String,
                   supply: List<Item> = DEFAULT_SUPPLY,
                   limits: Limits = DEFAULT_LIMITS,
                   playerHitPoints: Int = DEFAULT_PLAYER_HIT_POINTS): Int? {
    val boss = Player.parse(bossStats)
    val store = Store(supply)
    return store.getPossibleInventories(limits).map {
        val hero = Player(playerHitPoints, it)
        val res = hero.battle(boss)
        BattleInfo(hero, boss, it, res)
    }.filter {
        it.res.win
    }.minByOrNull {
        it.inventory.cost
    }?.inventory?.cost
}

fun day21Part1() {
    println(Player(hitPoints=100, damage=7, armor=4).battle(Player(hitPoints=109, damage=8, armor=2)))
    println("day21/part1/rpg: ${
        findMinWinCost(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day21/boss.txt").bufferedReader().readText()
        )
    }")
}