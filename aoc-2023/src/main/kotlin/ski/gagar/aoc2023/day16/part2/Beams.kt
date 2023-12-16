package ski.gagar.aoc2023.day16.part2

import ski.gagar.aoc2023.day16.part1.*


fun BeamField.allStartsBorder() = sequence {
    for (y in 0 until height) {
        val l = Coordinates(0, y)
        val r = Coordinates(width - 1, y)

        yield(BeamPosition(l, Direction.RIGHT))

        yield(BeamPosition(r, Direction.LEFT))
    }

    for (x in 0 until width) {
        val t = Coordinates(x, 0)
        val b = Coordinates(x, height - 1)

        yield(BeamPosition(t, Direction.DOWN))

        yield(BeamPosition(b, Direction.UP))
    }
}

fun maxEnergizedTiles(lines: Sequence<String>): Int {
    val field = BeamField.from(lines)
    return field.allStartsBorder().maxOf { field.getEnergized(it).size }
}