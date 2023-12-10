package ski.gagar.aoc2022.day18.part1

import ski.gagar.aoc.util.getResourceAsStream

/**
 * Let's consider it a max coords of the cube
 */
data class Cube(val x: Int, val y: Int, val z: Int) {
    fun sides() = sequence {
        yield(CubeSide(x, y, z, Plane.XY))
        yield(CubeSide(x, y, z - 1, Plane.XY))
        yield(CubeSide(x, y, z, Plane.XZ))
        yield(CubeSide(x, y - 1, z, Plane.XZ))
        yield(CubeSide(x, y, z, Plane.YZ))
        yield(CubeSide(x - 1, y, z, Plane.YZ))
    }.toSet()

    companion object {
        fun parse(str: String): Cube {
            val parts = str.split(",")
            require(parts.size == 3)
            return Cube(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}

enum class Plane {
    XY, XZ, YZ
}

data class CubeSide(val x: Int, val y: Int, val z: Int, val plane: Plane) {
    fun area() = 1
}

fun processCubes(lines: Sequence<String>): Int {
    val sides = mutableMapOf<CubeSide, Int>()

    for (line in lines) {
        val cube = Cube.parse(line)

        for (side in cube.sides()) {
            sides[side] = (sides[side] ?: 0) + 1
        }
    }

    return sides.asSequence().filter { (k, v) -> v == 1 }.sumOf { (k, _) -> k.area() }
}
