package ski.gagar.aoc2022.day8.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.util.BitSet

class Forest(private val trees: List<List<Int>>) {
    val height: Int
    val width: Int
    private val visibility: BitSet

    init {
        require(trees.asSequence().map { it.size }.toSet().size == 1)
        require(trees.first().isNotEmpty())
        height = trees.size
        width = trees.first().size
        visibility = BitSet(trees.size * trees.first().size)
        initVisibility()
    }

    private fun processTreeVisibility(x: Int, y: Int, tallestSoFar: Int): Int {
        val current = getHeight(x, y)
        if (current > tallestSoFar) {
            visibility[index(x, y)] = true
            return current
        }
        return tallestSoFar
    }

    private fun initVisibility() {
        for (y in 0 until height) {
            var tallestSoFar = Int.MIN_VALUE
            for (x in 0 until width) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
            tallestSoFar = Int.MIN_VALUE
            for (x in width - 1 downTo  0) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }

        }
        for (x in 0 until width) {
            var tallestSoFar = Int.MIN_VALUE
            for (y in 0 until height) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
            tallestSoFar = Int.MIN_VALUE
            for (y in height - 1 downTo  0) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
        }
    }

    private fun checkBounds(x: Int, y: Int) {
        require(x in 0 until width)
        require(y in 0 until height)
    }

    private fun index(x: Int, y: Int): Int {
        checkBounds(x, y)
        return y * width + x
    }

    fun getHeight(x: Int, y: Int): Int {
        checkBounds(x, y)
        return trees[y][x]
    }

    fun isVisible(x: Int, y: Int): Boolean {
        checkBounds(x, y)
        return visibility[index(x, y)]
    }

    fun nVisible() = visibility.cardinality()
}


fun nVisible(strings: Sequence<String>) =
    Forest(strings.map { it.map { c -> c - '0' } }.toList()).nVisible()

fun day8Part1() {
    println("day8/part1/forest: ${
        nVisible(getResourceAsStream("/ski.gagar.aoc.aoc2022.day8/forest.txt").bufferedReader().lineSequence())
    }")
}
