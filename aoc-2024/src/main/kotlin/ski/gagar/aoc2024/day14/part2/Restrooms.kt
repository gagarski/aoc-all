package ski.gagar.aoc2024.day14.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc2024.day14.part1.Coordinates
import ski.gagar.aoc2024.day14.part1.Robot
import ski.gagar.aoc2024.day14.part1.RobotsParser
import java.io.File
import java.math.BigInteger

fun Robot.runNaive(field: Coordinates): Robot {
    var row = (this.start.row + velocity.row)
    var column = (this.start.column + velocity.column)

    if (row < BigInteger.ZERO) {
        row += field.row
    } else if (row >= field.row) {
        row -= field.row
    }

    if (column < BigInteger.ZERO) {
        column += field.column
    } else if (column >= field.column) {
        column -= field.column
    }


    return Robot(Coordinates(column, row), velocity)
}

fun Int.toDigitOrPlus() =
    when (this) {
        in 0..9 -> '0' + this
        else -> '+'
    }

fun List<Coordinates>.draw(field: Coordinates) = buildString {
    val counts = this@draw.groupingBy { it }.eachCount()
    val midRow = field.row / BigInteger.TWO
    val midColumn = field.column / BigInteger.TWO
    for (row in 0 until field.row.toInt()) {
        for (col in 0 until field.column.toInt()) {
            val count: Int = counts[Coordinates(col.toBigInteger(), row.toBigInteger())] ?: 0
            when {
//                row == midRow.toInt() || col == midColumn.toInt() -> append(" ")
                count == 0 -> append('.')
                else -> append(count.toDigitOrPlus())
            }
        }
        append('\n')
    }
}

private val F_EX = Coordinates(column = 11.toBigInteger(), row = 7.toBigInteger())
private val F_REAL = Coordinates(column = 101.toBigInteger(), row = 103.toBigInteger())

fun restRoomRobotsDraw(input: String, steps: Int = 30000,
                       field: Coordinates = F_REAL
): String {
    require(field.row % BigInteger.TWO == BigInteger.ONE)
    require(field.column % BigInteger.TWO == BigInteger.ONE)
    val robots = RobotsParser.parse(input)
    var endCoords = robots
    val out = File("out/Aoc2024/Day14.txt")
    out.parentFile.mkdirs()
    out.printWriter().use { writer ->
        for (step in 1 .. steps) {
            endCoords = endCoords.map { it.runNaive(field) }
            writer.println(step)
            writer.println(endCoords.map { it.start }.draw(field))
        }
    }


    return "See $out"
}
