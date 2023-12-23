package ski.gagar.aoc2023.day22.part1

import java.util.TreeMap

data class Coordinates(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Coordinates) = Coordinates(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Coordinates) = Coordinates(x - other.x, y - other.y, z - other.z)

    fun toTaskString() = "$x,$y,$z"
    companion object {
        fun from(string: String): Coordinates {
            val splitted = string.split(",")
            require(splitted.size == 3) {
                "Not a point"
            }
            return Coordinates(splitted[0].toInt(), splitted[1].toInt(), splitted[2].toInt())
        }
    }

}

infix fun IntRange.intersects(other: IntRange): Boolean {
    require(step == 1 && other.step == 1)
    val min = sequenceOf(this, other).minBy { it.start }
    val max = sequenceOf(this, other).maxBy { it.start }

    return max.first <= min.last
}

data class RectangleXy(val xRange: IntRange, val yRange: IntRange, val z: Int) {
    fun intersectsIgnoringZ(other: RectangleXy) =
        (xRange intersects other.xRange) && (yRange intersects other.yRange)
}

data class Cube(val lbd: Coordinates, val rfu: Coordinates, val label: String? = null) {
    init {
        require(lbd.x < rfu.x)
        require(lbd.y < rfu.y)
        require(lbd.y < rfu.y)
    }

    val up
        get() = RectangleXy(lbd.x..<rfu.x, lbd.y..<rfu.y, rfu.z)
    val down
        get() = RectangleXy(lbd.x..<rfu.x, lbd.y..<rfu.y, lbd.z)

    fun fall(z: Int): Cube {
        return Cube(lbd.copy(z = lbd.z - z), rfu.copy(z = rfu.z - z), label)
    }

    fun asOrigCoords(): Pair<Coordinates, Coordinates> = lbd - LBD_CORRECTION to rfu - RFU_CORRECTION

    fun toTaskString() = asOrigCoords().let { (first, second) ->
        val cubeItself = "${first.toTaskString()}~${second.toTaskString()}"

        if (label == null)
            cubeItself
        else
            "$cubeItself <- $label"
    }

    companion object {
        private val LBD_CORRECTION = Coordinates(0, 0, -1)
        private val RFU_CORRECTION = Coordinates(1, 1, 0)

        fun from(string: String): Cube {
            val splittedByArrow = string.split("<-").map { it.trim() }

            val splitted = splittedByArrow[0].split("~")
            val label = splittedByArrow.getOrNull(1)

            require(splitted.size == 2)
            val lbd = Coordinates.from(splitted[0]) + LBD_CORRECTION
            val rfu = Coordinates.from(splitted[1]) + RFU_CORRECTION
            return Cube(lbd, rfu, label)
        }
    }
}

class CubeBox(cubes: Collection<Cube>) {
    private var byUp: TreeMap<Int, MutableSet<Cube>> = TreeMap()
    private var byDown: TreeMap<Int, MutableSet<Cube>> = TreeMap()
    private var cubes_: MutableSet<Cube> = mutableSetOf()

    val cubes: Set<Cube>
        get() = cubes_

    init {
        for (cube in cubes) {
            cube.remember()
        }
    }

    private fun Cube.remember() {
        val upZ = up.z
        val downZ = down.z
        val upSet = byUp[upZ] ?: mutableSetOf()
        upSet.add(this)
        byUp[upZ] = upSet

        val downSet = byDown[downZ] ?: mutableSetOf()
        downSet.add(this)
        byDown[downZ] = downSet
        cubes_.add(this)
    }

    private fun Cube.forget() {
        byUp[up.z]?.remove(this)
        byDown[down.z]?.remove(this)
        cubes_.remove(this)
    }

    private fun Cube.fallAndUpdate(z: Int): Cube {
        if (z == 0) {
            remember()
            return this
        }

        forget()

        val new = fall(z)

        new.remember()
        return new
    }

    fun fall(fromInclusive: Int = 0) {
        val oldByDown = byDown.tailMap(fromInclusive, true)
        byDown = TreeMap()

        for ((downZ, cubes) in oldByDown) {
            cubes@ for (cube in cubes) {
                val belowIncl = byUp.headMap(downZ, true).reversed()

                for ((zBelow, cubesBelow_) in belowIncl) {
                    val cubesBelow = cubesBelow_ ?: mutableSetOf()
                    val blocking = cubesBelow.firstOrNull { it.up.intersectsIgnoringZ(cube.down) }

                    if (blocking != null) {
                        cube.fallAndUpdate(downZ - zBelow)
                        continue@cubes
                    }
                }
                cube.fallAndUpdate(downZ)
            }
        }
    }

    fun isDisintegratable(cube: Cube): Boolean {
        val possiblyBasing = byDown[cube.up.z] ?: return true
        val neighborsByUp = byUp[cube.up.z] ?: throw IllegalStateException("Should not happen")
        return possiblyBasing.all { b ->
            neighborsByUp.any { n -> n != cube && n.up.intersectsIgnoringZ(b.down)}
        }
    }

    companion object {
        fun from(lines: Sequence<String>) = CubeBox(lines.map { Cube.from(it) }.toList())
    }
}

fun nDisintegratable(lines: Sequence<String>): Int {
    val cubeBox = CubeBox.from(lines)
    cubeBox.fall()

//    println(0..0 intersects 2..2)

    cubeBox.cubes.count { cubeBox.isDisintegratable(it) }

    return cubeBox.cubes.count { cubeBox.isDisintegratable(it) }
}