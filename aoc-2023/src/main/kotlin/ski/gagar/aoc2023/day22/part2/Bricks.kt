package ski.gagar.aoc2023.day22.part2

import ski.gagar.aoc2023.day22.part1.Cube
import java.util.TreeMap

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

    fun fall(fromInclusive: Int = 0): Int {
        val oldByDown = byDown.tailMap(fromInclusive, true)
        byDown = TreeMap()
        var fallenBricks = 0

        for ((downZ, cubes) in oldByDown) {
            cubes@ for (cube in cubes) {
                val belowIncl = byUp.headMap(downZ, true).reversed()

                for ((zBelow, cubesBelow_) in belowIncl) {
                    val cubesBelow = cubesBelow_ ?: mutableSetOf()
                    val blocking = cubesBelow.firstOrNull { it.up.intersectsIgnoringZ(cube.down) }

                    if (blocking != null) {
                        val toFall = downZ - zBelow
                        cube.fallAndUpdate(toFall)
                        if (toFall != 0) {
                            fallenBricks++
                        }
                        continue@cubes
                    }
                }
                cube.fallAndUpdate(downZ)
                if (downZ != 0) {
                    fallenBricks++
                }
            }
        }
        return fallenBricks
    }

    fun isDisintegratable(cube: Cube): Boolean {
        val possiblyBasing = byDown[cube.up.z] ?: return true
        val neighborsByUp = byUp[cube.up.z] ?: throw IllegalStateException("Should not happen")
        return possiblyBasing.all { b ->
            neighborsByUp.any { n -> n != cube && n.up.intersectsIgnoringZ(b.down)}
        }
    }

    fun disintegrate(cube: Cube): Int {
        cube.forget()
        return fall(cube.down.z)
    }

    companion object {
        fun from(lines: Sequence<String>) = CubeBox(lines.map { Cube.from(it) }.toList())
    }
}

fun sumFallen(lines: Sequence<String>): Int {
    val cubes = lines.map { Cube.from(it) }.toList()
    var sum = 0

    val main = CubeBox(cubes)
    main.fall()

    for (cube in main.cubes) {
        val testArea = CubeBox(main.cubes)
        testArea.fall()
        sum += testArea.disintegrate(cube)
    }

    return sum
}