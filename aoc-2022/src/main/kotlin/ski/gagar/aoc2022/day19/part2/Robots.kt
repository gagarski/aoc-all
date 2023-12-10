package ski.gagar.aoc2022.day19.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day19.part1.*

fun List<Blueprint>.quality(
    limit: Int = 32,
    take: Int = 3,
    initialRobots: Map<Robot, Int> = mapOf(Robot(Currency.ORE, Price.FREE) to 1),
    targetCurrency: Currency = Currency.GEODE
): Int {
    var q = 1

    for (bp in this.take(take)) {
        q *= (bp.bestCourseOfAction(limit, initialRobots, targetCurrency).result.amounts[targetCurrency] ?: 0)
    }

    return q
}