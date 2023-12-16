package ski.gagar.aoc2023.day14.part2

import ski.gagar.aoc2023.day14.part1.Direction
import ski.gagar.aoc2023.day14.part1.ReflectorDish

fun ReflectorDish.spin() =
    this
        .tilt(Direction.NORTH)
        .tilt(Direction.WEST)
        .tilt(Direction.SOUTH)
        .tilt(Direction.EAST)


fun loadAfterSpin(lines: Sequence<String>,
                  cycles: Long = 1000000000,
                  loadDir: Direction = Direction.NORTH): Int {
    var dish = ReflectorDish.from(lines)
    val seenStates = mutableMapOf(dish to 0L)

    var processed = 0L
    var loop: Long? = null

    for (i in 0 until cycles) {
        dish = dish.spin()
        processed = i + 1
        val seen = seenStates[dish]
        if (seen != null) {
            loop = i - seen
            break
        }
        seenStates[dish] = i
    }

    if (loop != null) {
        val left = cycles - processed
        val loopsToSkip = left / loop
        processed += loopsToSkip * loop
    }

    for (i in processed until cycles) {
        dish = dish.spin()
    }

    return dish.loadOn(loadDir)
}