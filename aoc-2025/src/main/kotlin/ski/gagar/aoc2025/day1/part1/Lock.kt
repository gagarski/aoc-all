package ski.gagar.aoc2025.day1.part1

fun parseStep(string: String): Int {
    require(string.length >= 2)
    val sign = when (string[0]) {
        'L' -> -1
        'R' -> 1
        else -> error("Invalid direction ${string[0]}")
    }
    val abs = string.substring(1).toInt()
    return sign * abs
}

fun countZeros(lines: Sequence<String>, start: Int = 50, modulo: Int = 100, target: Int = 0): Int {
    var position = start
    var zeros = 0
    for (line in lines) {
        val step = parseStep(line)
        position = (position + step).mod(modulo)
        if (position == target) {
            zeros++
        }
    }
    return zeros
}