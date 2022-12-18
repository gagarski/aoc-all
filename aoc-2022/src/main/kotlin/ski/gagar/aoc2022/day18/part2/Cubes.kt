package ski.gagar.aoc2022.day18.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day18.part1.Cube
import ski.gagar.aoc2022.day18.part1.CubeSide
import java.util.BitSet

class UnionFind(private val size: Int) {
    private val ids = mutableMapOf<Int, Int>()
    var count = size
        private set

    fun find(p: Int): Int {
        require(p < size)
        return ids[p] ?: p
    }

    fun union(p: Int, q: Int) {
        require(p < size)
        require(q < size)

        val pId = ids[p] ?: p
        val qId = ids[q] ?: q

        if (pId == qId) return

        ids[pId] = qId

        for ((k, v) in ids) {
            if (v == pId) ids[k] = qId
        }

        count--
    }

    fun connected(p: Int, q: Int): Boolean {
        require(p < size)
        require(q < size)
        return (ids[p] ?: p) == (ids[q] ?:q)
    }
}

class CubeGrid(val cubes: Set<Cube>) {
    val opened: BitSet
    val unionFind: UnionFind
    val xSize: Int
    val ySize: Int
    val zSize: Int
    private val xOffset: Int
    private val yOffset: Int
    private val zOffset: Int

    init {
        xOffset = cubes.minOf { it.x }
        yOffset = cubes.minOf { it.y }
        zOffset = cubes.minOf { it.z }
        xSize = cubes.maxOf { it.x } - xOffset + 1
        ySize = cubes.maxOf { it.y } - yOffset + 1
        zSize = cubes.maxOf { it.z } - zOffset + 1

        opened = BitSet(xSize * ySize * zSize)
        unionFind = UnionFind(xSize * ySize * zSize + 1)

        for (z in 0 until zSize) {
            for (y in 0 until ySize) {
                for (x in 0 until xSize) {
                    if (Cube(x + xOffset, y + yOffset, z + zOffset) !in cubes) {
                        openAndConnect(x, y, z)
                    }
                }
            }
        }

    }

    private fun openAndConnect(x: Int, y: Int, z: Int) {
        opened[index(x, y, z)] = true
        doUnion(x, y, z, x - 1, y, z)
        doUnion(x, y, z, x + 1, y, z)
        doUnion(x, y, z, x, y - 1, z)
        doUnion(x, y, z, x, y + 1, z)
        doUnion(x, y, z, x, y, z - 1)
        doUnion(x, y, z, x, y, z + 1)
        doUnionWithSource(x, y, z)
    }

    private fun doUnion(x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int) {
        if (x2 !in 0 until xSize) return
        if (y2 !in 0 until ySize) return
        if (z2 !in 0 until zSize) return
        if (!opened[index(x2, y2, z2)]) return
        unionFind.union(index(x, y, z), index(x2, y2, z2))
    }

    private fun doUnionWithSource(x: Int, y: Int, z: Int) {
        if (isBoundary(x, y, z)) unionFind.union(index(x, y, z), sourceIndex())
    }

    private fun index(x: Int, y: Int, z: Int) = z * xSize * ySize + y * xSize + x
    private fun sourceIndex() = xSize * ySize * zSize

    private fun isBoundary(x: Int, y: Int, z: Int) =
        x == 0 || x == xSize - 1 || y == 0 || y == ySize - 1 || z == 0 || z == zSize - 1

    fun cavities() = sequence {
        for (z in 0 until zSize) {
            for (y in 0 until ySize) {
                for (x in 0 until xSize) {
                    if (opened[index(x, y, z)] && !unionFind.connected(index(x, y, z), sourceIndex())) {
                        yield(Cube(x + xOffset, y + yOffset, z + zOffset))
                    }
                }
            }
        }
    }.toSet()
}


fun processCubes(lines: Sequence<String>): Int {
    val sides = mutableMapOf<CubeSide, Int>()
    val cubes = mutableSetOf<Cube>()

    for (line in lines) {
        val cube = Cube.parse(line)

        cubes.add(cube)

        for (side in cube.sides()) {
            sides[side] = (sides[side] ?: 0) + 1
        }
    }

    val grid = CubeGrid(cubes)

    for (cavity in grid.cavities()) {
        for (side in cavity.sides()) {
            sides.remove(side)
        }
    }

    return sides.asSequence().filter { (k, v) -> v == 1 }.sumOf { (k, _) -> k.area() }
}

fun day18Part2() {
    println("day18/part2/cubes: ${
        processCubes(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day18/cubes.txt").bufferedReader().lineSequence()
        )
    }")
}
