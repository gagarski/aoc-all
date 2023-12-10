package ski.gagar.aoc2015.day21.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day21.part1.*

fun findMaxLoseCost(bossStats: String,
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
        !it.res.win
    }.maxByOrNull {
        it.inventory.cost
    }?.inventory?.cost
}
