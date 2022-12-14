package ski.gagar.aoc2015.day20.part2

import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun sumDivisorsWithLimit(n: Int, limit: Int = 50): Int {
    val sqrtFloor = floor(sqrt(n.toDouble())).toInt()
    var ctr = 0
    for (i in 1..sqrtFloor) {
        if (n % i == 0) {
            if (n / i <= limit) ctr += i
            val opposite = n / i
            if (opposite != i && n / opposite <= limit) ctr += opposite
        }
    }

    return ctr
}

fun firstHouseWithPresentsCount(target: Int, presentsPerHouse: Int = 11, limit: Int = 50) =
    generateSequence(1) { it + 1 }.first { sumDivisorsWithLimit(it, limit) * presentsPerHouse >= target }

fun day20Part2() {
    println("day20/part2/houses: ${
        firstHouseWithPresentsCount(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day20/houses.txt").bufferedReader().lineSequence().first().toInt()
        )
    }")
}
