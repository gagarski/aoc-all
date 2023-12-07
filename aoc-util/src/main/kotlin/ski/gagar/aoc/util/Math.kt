package ski.gagar.aoc.util

infix fun Long.pow(power: Int): Long {
    var p = 1L
    for (i in 0 until power) {
        p *= this
    }
    return p
}
