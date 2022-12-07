package ski.gagar.aoc2015.day6.part2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class LightGridTest {
    fun checkOutOfBounds(width: Int, height: Int, coordinatesConsumer: (Int, Int) -> Unit) {
        assertThrows(IllegalArgumentException::class.java) {
            coordinatesConsumer(-1, 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            coordinatesConsumer(0, -1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            coordinatesConsumer(0, height + 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            coordinatesConsumer(width + 1, 0)
        }

        coordinatesConsumer(width - 1, height - 1)
        coordinatesConsumer(0, 0)
    }

    private val Boolean.onOff
        get() = if (this) "on" else "off"

    fun assertGrid(expectedState: List<List<Int>>, grid: LightGrid) {
        val widths = expectedState.map { it.size }.toSet()
        if (widths.size != 1) {
            throw IllegalArgumentException("Expected state supposed to be rectangular")
        }
        val width = widths.first()
        val height = expectedState.size

        assertEquals(width, grid.width)
        assertEquals(height, grid.height)

        for ((y, line) in expectedState.withIndex()) {
            for ((x, expectedBrightness) in line.withIndex()) {
                val brightness = grid.getBrightness(x, y)
                val expectedOn = expectedBrightness != 0
                val isOn = grid.isOn(x, y)
                assertEquals(expectedBrightness, brightness, "Brightness of bulb ($x, $y) is wrong")
                assertEquals(expectedOn, isOn, "Bulb ($x, $y) is ${isOn.onOff} but expected to be ${expectedOn.onOff}")
            }
        }
    }

    @Test
    fun bounds() {
        val grid = LightGrid(10, 10)
        checkOutOfBounds(10, 10) { x, y ->
            grid.turnOn(x, y)
        }
        checkOutOfBounds(10, 10) { x, y ->
            grid.turnOff(x, y)
        }
        checkOutOfBounds(10, 10) { x, y ->
            grid.toggle(x, y)
        }
        checkOutOfBounds(10, 10) { x, y ->
            grid.isOn(x, y)
        }
    }

    @Test
    fun singleLight() {
        val grid = LightGrid(10, 10)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOn(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOn(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOff(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOff(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.toggle(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.toggle(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 4, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
    }

    @Test
    fun initiallyOff() {
        val grid = LightGrid(10, 10)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
    }

    @Test
    fun bulkComplex() {
        val grid = LightGrid(10, 10)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOn(4..6, 5..7)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0),
                listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0),
                listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(9, grid.onCount)
        assertEquals(9, grid.totalBrightness)

        grid.turnOn(4..6, 5..7)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(9, grid.onCount)
        assertEquals(18, grid.totalBrightness)


        grid.turnOn(0 until 10, 0..2)
        assertGrid(
            listOf(
                listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(39, grid.onCount)
        assertEquals(48, grid.totalBrightness)

        grid.turnOn(0 until 10, 0..2)
        assertGrid(
            listOf(
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(39, grid.onCount)
        assertEquals(78, grid.totalBrightness)

        grid.turnOff(0 until 10, 0..0)
        assertGrid(
            listOf(
                listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(39, grid.onCount)
        assertEquals(68, grid.totalBrightness)

        grid.turnOff(0 until 10, 0..0)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(29, grid.onCount)
        assertEquals(58, grid.totalBrightness)

        grid.turnOff(7..8, 1..2)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(2, 2, 2, 2, 2, 2, 2, 1, 1, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 1, 1, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(29, grid.onCount)
        assertEquals(54, grid.totalBrightness)

        grid.turnOff(7..8, 1..2)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(2, 2, 2, 2, 2, 2, 2, 0, 0, 2),
                listOf(2, 2, 2, 2, 2, 2, 2, 0, 0, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(25, grid.onCount)
        assertEquals(50, grid.totalBrightness)


        grid.toggle(0 until 10, 1..3)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 4, 4, 4, 4, 4, 2, 2, 4),
                listOf(4, 4, 4, 4, 4, 4, 4, 2, 2, 4),
                listOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(39, grid.onCount)
        assertEquals(110, grid.totalBrightness)
        grid.toggle(0 until 10, 1..3)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(6, 6, 6, 6, 6, 6, 6, 4, 4, 6),
                listOf(6, 6, 6, 6, 6, 6, 6, 4, 4, 6),
                listOf(4, 4, 4, 4, 4, 4, 4, 4, 4, 4),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(39, grid.onCount)
        assertEquals(170, grid.totalBrightness)

        grid.toggle(3..7, 1..5)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(6, 6, 6, 8, 8, 8, 8, 6, 4, 6),
                listOf(6, 6, 6, 8, 8, 8, 8, 6, 4, 6),
                listOf(4, 4, 4, 6, 6, 6, 6, 6, 4, 4),
                listOf(0, 0, 0, 2, 2, 2, 2, 2, 0, 0),
                listOf(0, 0, 0, 2, 4, 4, 4, 2, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        assertEquals(46, grid.onCount)
        assertEquals(220, grid.totalBrightness)

        grid.toggle(3..7, 1..5)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0,  0,  0,  0,  0, 0, 0),
                listOf(6, 6, 6, 10, 10, 10, 10, 8, 4, 6),
                listOf(6, 6, 6, 10, 10, 10, 10, 8, 4, 6),
                listOf(4, 4, 4, 8,  8,  8,  8,  8, 4, 4),
                listOf(0, 0, 0, 4,  4,  4,  4,  4, 0, 0),
                listOf(0, 0, 0, 4,  6,  6,  6,  4, 0, 0),
                listOf(0, 0, 0, 0,  2,  2,  2,  0, 0, 0),
                listOf(0, 0, 0, 0,  2,  2,  2,  0, 0, 0),
                listOf(0, 0, 0, 0,  0,  0,  0,  0, 0, 0),
                listOf(0, 0, 0, 0,  0,  0,  0,  0, 0, 0),
            ), grid)
        assertEquals(46, grid.onCount)
        assertEquals(270, grid.totalBrightness)
    }

    @Test
    fun underflow() {
        val grid = LightGrid(10, 10)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.turnOff(4, 8)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.dim(4, 8, Int.MAX_VALUE)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)

        grid.turnOff(0 until 10, 2..3)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
        grid.dim(0 until 10, 2..3, Int.MAX_VALUE)
        assertGrid(
            listOf(
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ), grid)
    }

    @Test
    fun overflow() {
        val grid = LightGrid(10, 10)
        grid.setBrightness(0 until 10, 0 until 10, Int.MAX_VALUE)
        assertGrid(
            listOf(
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
            ), grid)
        grid.turnOn(4, 8)
        assertGrid(
            listOf(
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
            ), grid)
        grid.toggle(4, 8)
        assertGrid(
            listOf(
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
            ), grid)
        grid.brighten(4, 8, Int.MAX_VALUE)
        assertGrid(
            listOf(
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
                listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
            ), grid)
    }
}
