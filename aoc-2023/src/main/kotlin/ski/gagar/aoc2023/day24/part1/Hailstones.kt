package ski.gagar.aoc2023.day24.part1

import ski.gagar.aoc.util.math.LinearEquation
import ski.gagar.aoc.util.math.LinearEquationSystem
import ski.gagar.aoc.util.math.NaturalFraction
import ski.gagar.aoc.util.math.nf
import java.math.BigInteger


data class Point(val x: NaturalFraction, val y: NaturalFraction)

/**
 * Represents a line using parametric line equation
 * x = x0 + ax * t
 * y = y0 + ay * t
 * Think of adding some invariants
 */
data class Line(
    val x0: NaturalFraction,
    val y0: NaturalFraction,
    val ax: NaturalFraction,
    val ay: NaturalFraction
) {
    fun pointAt(t: NaturalFraction) = Point(x0 + ax * t, y0 + ay * t)

    fun parameterAtPoint(point: Point): NaturalFraction {
        require(!ax.isZero || x0 == point.x) {
            "$point is not on the $this"
        }
        require(!ay.isZero || y0 == point.y) {
            "$point is not on the $this"
        }


        val tX = (point.x - x0) / ax
        val tY = (point.y - y0) / ay

        val nonInf = sequenceOf(tX, tY).filter { !it.isInfinity }.toList()

        check(nonInf.isNotEmpty())

        require(nonInf.all { it == nonInf.first() }) {
            "$point is not on the $this"
        }

        return tX
    }

    private fun linearEquation() = LinearEquation(ay, -ax, free = x0 * ay - y0 * ax)

    fun intersection(other: Line): Point? {
        val sys = LinearEquationSystem(
            linearEquation(),
            other.linearEquation()
        )

        return sys.solve()?.let {
            Point(it[0], it[1])
        }
    }

    companion object {
        fun from(string: String): Line {
            val startAndVelocity = string.split("@").map { it.trim() }
            require(startAndVelocity.size == 2)
            val start = startAndVelocity[0].split(",").map { it.trim().toLong() }
            val velocity = startAndVelocity[1].split(",").map { it.trim().toLong() }
            require(start.size == 3)
            require(velocity.size == 3)
            return Line(
                nf(start[0]),
                nf(start[1]),
                nf(velocity[0]),
                nf(velocity[1])
            )
        }
    }
}

fun countIntersectingInFuture(
    strings: Sequence<String>,
    min: NaturalFraction = nf(BigInteger("200000000000000")),
    max: NaturalFraction = nf(BigInteger("400000000000000"))
    ): Int {
    val lines = strings.map { Line.from(it) }.toList()

    var cnt = 0

    for (i in lines.indices) {
        for (j in i + 1 .. lines.lastIndex) {
            val int = lines[i].intersection(lines[j])
            if (int != null &&
                int.x >= min && int.x <= max && int.y >= min && int.y <= max &&
                lines[i].parameterAtPoint(int) > NaturalFraction.ZERO &&
                lines[j].parameterAtPoint(int) > NaturalFraction.ZERO) {
                cnt++
            }
        }
    }

    return cnt
}