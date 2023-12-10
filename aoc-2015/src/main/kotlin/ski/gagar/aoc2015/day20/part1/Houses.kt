package ski.gagar.aoc2015.day20.part1

import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun sumDivisors(n: Int): Int {
    val sqrtFloor =  floor(sqrt(n.toDouble())).toInt()
    var ctr = 0
    for (i in 1..sqrtFloor) {
        if (n % i == 0) {
            ctr += i
            val opposite = n / i
            if (opposite != i) ctr += opposite
        }
    }

    return ctr
}

fun firstHouseWithPresentsCount(target: Int, presentsPerHouse: Int = 10) =
    generateSequence(1) { it + 1 }.first { sumDivisors(it) * presentsPerHouse >= target }
