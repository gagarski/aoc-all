package ski.gagar.aoc2023.day24.part2

import ski.gagar.aoc.util.math.LinearEquation
import ski.gagar.aoc.util.math.LinearEquationSystem
import ski.gagar.aoc.util.math.NaturalFraction
import ski.gagar.aoc.util.math.nf

data class Point(val x: NaturalFraction, val y: NaturalFraction, val z: NaturalFraction)

/**
 * Represents a line using parametric line equation
 * x = x0 + ax * t
 * y = y0 + ay * t
 * z = z0 + az * t
 */
data class Line(
    val x0: NaturalFraction,
    val y0: NaturalFraction,
    val z0: NaturalFraction,
    val ax: NaturalFraction,
    val ay: NaturalFraction,
    val az: NaturalFraction) {

    init {
        require(sequenceOf(ax, ay, ax).filter { it.isZero }.count() <= 1)
    }

    fun pointAt(t: NaturalFraction) = Point(x0 + ax * t, y0 + ay * t, z0 + az * t)

    fun onTheSamePlane(other: Line): Boolean {
        val slopes =
            listOf(this.ax / other.ax, this.ay / other.ay, this.az / other.az).filter { !it.isInfinity }

        if (slopes.all { it == slopes.first() }) return true

        return this.intersection(other) != null
    }

    fun parameterAtPoint(point: Point): NaturalFraction {
        require(!ax.isZero || x0 == point.x) {
            "$point is not on the $this"
        }
        require(!ay.isZero || y0 == point.y) {
            "$point is not on the $this"
        }
        require(!az.isZero || z0 == point.z) {
            "$point is not on the $this"
        }

        val tX = (point.x - x0) / ax
        val tY = (point.y - y0) / ay
        val tZ = (point.z - z0) / az

        val nonInf = sequenceOf(tX, tY, tZ).filter { !it.isInfinity }.toList()

        check(nonInf.size >= 2)

        require(nonInf.all { it == nonInf.first() }) {
            "$point is not on the $this"
        }

        return tX
    }

    private val equationXy: LinearEquation
        get() {
            return LinearEquation(
                ay, -ax, NaturalFraction.ZERO, free = x0 * ay - y0 * ax
            )
        }

    private val equationYz: LinearEquation
        get() {
            return LinearEquation(
                NaturalFraction.ZERO, az, -ay, free = y0 * az - z0 * ay
            )
        }

    private val equationXz: LinearEquation
        get() {
            return LinearEquation(
                az, NaturalFraction.ZERO, -ax, free = x0 * az - z0 * ax
            )
        }

    val equationSystem: LinearEquationSystem
        get() {
            return LinearEquationSystem(
                equationXy,
                equationXz,
                equationYz
            )
        }

    val equationSystemForLineFit: LinearEquationSystem
        get() {
            // 12 vars
            // 0 - x0'
            // 1 - ax'
            // 2 - y0'
            // 3 - ay'
            // 4 - z0'
            // 5 - az'
            // Multiplicands, necessary for solving
            // 6 - ay' * x0'
            // 7 - ax' * y0'
            // 8 - az' & y0'
            // 9 - ay' * z0'
            // 10 - ax' * z0'
            // 11 - az' * x0'
            return LinearEquationSystem(
                LinearEquation(
                    -ay, y0, ax, -x0, nf(0), nf(0),
                    nf(1), nf(-1), nf(0), nf(0), nf(0), nf(0),
                    free = ax * y0 - ay * x0
                ),
                LinearEquation(
                    nf(0), nf(0), -az, z0, ay, -y0,
                    nf(0), nf(0), nf(1), nf(-1), nf(0), nf(0),
                    free = ay * z0 - az * y0
                ),
                LinearEquation(
                    az, -z0, nf(0), nf(0), -ax, x0,
                    nf(0), nf(0), nf(0), nf(0), nf(1), nf(-1),
                    free = az * x0 - ax * z0
                )
                // 12 - var (can we do better, I only thought about reducing it to 10)
                // 0 - x0'
                // 1 - ax'
                // 2 - y0'
                // 3 - ay'
                // 4 - z0'
                // 5 - az'
                // Multiplicands, necessary for solving
                // 6 - ay' * x0'
                // 7 - ax' * y0'
                // 8 - az' & y0'
                // 9 - ay' * z0'
                // 10 - ax' * z0'
                // 11 - az' * x0'
            )
        }

    fun intersection(other: Line): Point? {
        val sys = this.equationSystem.combine(other.equationSystem)
        val roots = sys.solve() ?: return null
        return Point(roots[0], roots[1], roots[2])
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
                nf(start[0]), nf(start[1]), nf(start[2]),
                nf(velocity[0]), nf(velocity[1]), nf(velocity[2])
            )
        }
    }
}


fun pickLines(lines: List<Line>): List<Line> {
    val res = mutableListOf<Line>()

    for (candidate in lines) {
        if (res.any { candidate.onTheSamePlane(it) })
            continue
        res.add(candidate)

        if (res.size == 4)
            break
    }

    check(res.size == 4)

    return res
}

fun fitLine(lines: List<Line>): NaturalFraction {
    require(lines.size == 4)

    val eqS = lines.fold<Line, LinearEquationSystem?>(null) { acc, line ->
        acc?.combine(line.equationSystemForLineFit) ?: line.equationSystemForLineFit
    }!!


    val reduced = eqS.reduce()
    // The equation system is singular and therefore not solved, however we can extract first six roots!
    require(reduced.equations[0].isSingleton)
    require(reduced.equations[2].isSingleton)
    require(reduced.equations[4].isSingleton)
    val x0 = reduced.equations[0].free / reduced.equations[0][0]
    val y0 = reduced.equations[2].free / reduced.equations[2][2]
    val z0 = reduced.equations[4].free / reduced.equations[4][4]
    return x0 + y0 + z0
}

fun getThrowingLine(strings: Sequence<String>): NaturalFraction {
    val lines = strings.map { Line.from(it) }.toList()
    val picked = pickLines(lines)

    return fitLine(picked)
}
