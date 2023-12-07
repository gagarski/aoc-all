package ski.gagar.aoc2022.day22.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import ski.gagar.aoc.util.getResourceAsStream
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.absoluteValue
import kotlin.math.sign

data class FieldRow(val offset: Int, val size: Int, val contents: BitSet)

data class Coordinates(val x: Int, val y: Int)
data class Coordinates3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Coordinates3D) = Coordinates3D(x + other.x, y + other.y, z + other.z)
}

enum class SweepConnectionType {
    TOP {
        override val opposite: SweepConnectionType
            get() = BOTTOM

        override fun realDir(transposed: Boolean, xDirection: Int, yDirection: Int): SweepConnectionType = when {
            !transposed && yDirection >= 0 -> TOP
            !transposed && yDirection < 0 -> BOTTOM
            xDirection >= 0 -> RIGHT
            else -> LEFT
        }
    },
    BOTTOM {
        override val opposite: SweepConnectionType
            get() = TOP

        override fun realDir(transposed: Boolean, xDirection: Int, yDirection: Int): SweepConnectionType = when {
            !transposed && yDirection >= 0 -> BOTTOM
            !transposed && yDirection < 0 -> TOP
            xDirection >= 0 -> LEFT
            else -> RIGHT
        }
    },
    LEFT {
        override val opposite: SweepConnectionType
            get() = RIGHT

        override fun realDir(transposed: Boolean, xDirection: Int, yDirection: Int) = when {
            !transposed && xDirection >= 0 -> LEFT
            !transposed && xDirection < 0 -> RIGHT
            yDirection >= 0 -> BOTTOM
            else -> TOP
        }
    },
    RIGHT {
        override val opposite: SweepConnectionType
            get() = LEFT

        override fun realDir(transposed: Boolean, xDirection: Int, yDirection: Int) = when {
            !transposed && xDirection >= 0 -> RIGHT
            !transposed && xDirection < 0 -> LEFT
            yDirection >= 0 -> TOP
            else -> BOTTOM
        }
    };

    abstract val opposite: SweepConnectionType

    fun isOppositeTo(other: SweepConnectionType) = this.opposite == other
    abstract fun realDir(transposed: Boolean, xDirection: Int, yDirection: Int): SweepConnectionType
}

enum class FacetType {
    TOP {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BACK
            SweepConnectionType.BOTTOM -> FRONT
            SweepConnectionType.LEFT -> LEFT
            SweepConnectionType.RIGHT -> RIGHT
        }
    },
    BOTTOM {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BACK
            SweepConnectionType.BOTTOM -> FRONT
            SweepConnectionType.LEFT -> LEFT
            SweepConnectionType.RIGHT -> RIGHT
        }
    },
    FRONT {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BOTTOM
            SweepConnectionType.BOTTOM -> TOP
            SweepConnectionType.LEFT -> LEFT
            SweepConnectionType.RIGHT -> RIGHT
        }
    },
    BACK {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BOTTOM
            SweepConnectionType.BOTTOM -> TOP
            SweepConnectionType.LEFT -> LEFT
            SweepConnectionType.RIGHT -> RIGHT
        }
    },
    LEFT {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BOTTOM
            SweepConnectionType.BOTTOM -> TOP
            SweepConnectionType.LEFT -> BACK
            SweepConnectionType.RIGHT -> FRONT
        }
    },
    RIGHT {
        override fun neighbor(realDir: SweepConnectionType) = when (realDir) {
            SweepConnectionType.TOP -> BOTTOM
            SweepConnectionType.BOTTOM -> TOP
            SweepConnectionType.LEFT -> BACK
            SweepConnectionType.RIGHT -> FRONT
        }
    };

    abstract fun neighbor(realDir: SweepConnectionType): FacetType
}

data class CubeFacet(
    val type: FacetType,
    val sweepStartX: Int,
    val sweepEndInclusiveX: Int,
    val sweepStartY: Int,
    val sweepEndInclusiveY: Int,
    val transposed: Boolean
) {
    val xSweepDirection= (sweepEndInclusiveX + 1 - sweepStartX).sign
    val ySweepDirection = (sweepEndInclusiveY + 1 - sweepStartY).sign

    fun coordinatesOnSweep(xOrY: Int, yOrZ: Int): Coordinates {
        val x = when {
            !transposed && xSweepDirection == 1 -> sweepStartX + xOrY
            !transposed && xSweepDirection == -1 -> sweepStartX - xOrY
            transposed && xSweepDirection == 1 -> sweepStartX + yOrZ
            transposed && xSweepDirection == -1 -> sweepStartX - yOrZ
            else -> throw IllegalStateException("Should not happen")
        }

        val y = when {
            !transposed && ySweepDirection == 1 -> sweepStartY + yOrZ
            !transposed && ySweepDirection == -1 -> sweepStartY - yOrZ
            transposed && ySweepDirection == 1 -> sweepStartY + xOrY
            transposed && ySweepDirection == -1 -> sweepStartY - xOrY
            else -> throw IllegalStateException("Should not happen")
        }
        return Coordinates(x, y)
    }

    fun correctFlatDirection(flatDir: FlatDirection): FlatDirection {
        val sweepDir = when (flatDir) {
            FlatDirection.UP, FlatDirection.DOWN -> if (!transposed) ySweepDirection else xSweepDirection
            else -> if (!transposed) xSweepDirection else ySweepDirection
        }

        val afterTranspose = when {
            !transposed -> flatDir
            else -> {
                when (flatDir) {
                    FlatDirection.UP -> FlatDirection.LEFT
                    FlatDirection.DOWN -> FlatDirection.RIGHT
                    FlatDirection.LEFT -> FlatDirection.UP
                    FlatDirection.RIGHT -> FlatDirection.DOWN
                }
            }
        }

        return when {
            sweepDir == 1 -> afterTranspose
            else -> afterTranspose.opposite
        }
    }

    val width = when (transposed) {
        false -> (sweepEndInclusiveX - sweepStartX).absoluteValue + 1
        true -> (sweepEndInclusiveY - sweepStartY).absoluteValue + 1
    }

    val height = when (transposed) {
        false -> (sweepEndInclusiveY - sweepStartY).absoluteValue + 1
        true -> (sweepEndInclusiveX - sweepStartX).absoluteValue + 1
    }
}

data class Cube(
    val facets: Map<FacetType, CubeFacet>
) {
    init {
        require(FacetType.values().all { it in facets })
    }

    val width = facets[FacetType.BOTTOM]!!.let {
        if (!it.transposed) it.width else it.height
    }

    val depth = facets[FacetType.BOTTOM]!!.let {
        if (!it.transposed) it.height else it.width
    }

    val height = facets[FacetType.FRONT]!!.let {
        if (!it.transposed) it.height else it.width
    }
}

data class SweepRectangle(
    val xRange: IntRange,
    val yRange: IntRange
) {
    val leftSide = SweepVerticalSection(xRange.first, yRange)
    val leftOfLeftSide = SweepVerticalSection(xRange.first - 1, yRange)
    val rightSide = SweepVerticalSection(xRange.last, yRange)
    val rightOfRightSide = SweepVerticalSection(xRange.last + 1, yRange)

    val topSide = SweepHorizontalSection(xRange, yRange.first)
    val aboveTopSide = SweepHorizontalSection(xRange, yRange.first - 1)
    val bottomSide = SweepHorizontalSection(xRange, yRange.last)
    val belowBottomSide = SweepHorizontalSection(xRange, yRange.last + 1)

    val width = xRange.last - xRange.first + 1
    val height = yRange.last - yRange.first + 1
}

interface SweepSection

data class SweepHorizontalSection(val xRange: IntRange, val y: Int) : SweepSection
data class SweepVerticalSection(val x: Int, val yRange: IntRange): SweepSection


private fun List<FieldRow>.getVerticalLines(): List<Int> {
    val minXOffset = minOf { it.offset }
    val maxXOffsetExclusive = maxOf { it.offset + it.size }

    val minYOffset = asSequence().takeWhile { it.size == 0 }.count()
    val maxYOffsetExclusive =
        minYOffset + size - asReversed().asSequence().takeWhile { it.size == 0 }.count()

    // First, let's detect lines in the sweep

    val verticalLines = mutableListOf<Int>()

    var prevVerticalRanges: List<IntRange> = listOf()
    for (x in minXOffset until maxXOffsetExclusive) {
        var lineStart: Int? = null
        val ranges = mutableListOf<IntRange>()

        for (y in minYOffset until maxYOffsetExclusive) {
            val point = get(x, y)

            when {
                point != null && lineStart == null -> lineStart = y
                point == null && lineStart != null -> {
                    ranges.add(lineStart until y - 1)
                    lineStart = null
                }
            }
        }

        if (lineStart != null) {
            ranges.add(lineStart until maxXOffsetExclusive)
        }

        if (prevVerticalRanges != ranges) {
            verticalLines.add(x)
        }
        prevVerticalRanges = ranges
    }

    verticalLines.add(maxXOffsetExclusive)
    return verticalLines
}

private fun List<FieldRow>.getHorizontalLines(): List<Int> {
    val minXOffset = minOf { it.offset }
    val maxXOffsetExclusive = maxOf { it.offset + it.size }

    val minYOffset = asSequence().takeWhile { it.size == 0 }.count()
    val maxYOffsetExclusive =
        minYOffset + size - asReversed().asSequence().takeWhile { it.size == 0 }.count()

    val horizontalLines = mutableListOf<Int>()
    var prevHorizontalRanges: List<IntRange> = listOf()
    for (y in minYOffset until maxYOffsetExclusive) {
        var lineStart: Int? = null
        val ranges = mutableListOf<IntRange>()

        for (x in minXOffset until maxXOffsetExclusive) {
            val point = get(x, y)

            when {
                point != null && lineStart == null -> lineStart = x
                point == null && lineStart != null -> {
                    ranges.add(lineStart until x - 1)
                    lineStart = null
                }
            }
        }

        if (lineStart != null) {
            ranges.add(lineStart until maxYOffsetExclusive)
        }

        if (prevHorizontalRanges != ranges) {
            horizontalLines.add(y)
        }
        prevHorizontalRanges = ranges
    }

    horizontalLines.add(maxYOffsetExclusive)
    return horizontalLines
}

private fun SweepRectangle.isFilled(sweep: List<FieldRow>): Boolean {
    var allFilled = true
    var noneFilled = true

    require(!xRange.isEmpty() && !yRange.isEmpty())

    for (y in yRange) {
        for (x in xRange) {
            val point = sweep.get(x, y)
            when (point) {
                null -> allFilled = false
                else -> noneFilled = false
            }
            check(noneFilled != allFilled)
        }
    }

    return allFilled
}

private fun List<FieldRow>.get(x: Int, y: Int): Boolean? {
    if (y !in indices) return null
    val row = this[y]
    if (x !in row.offset until row.offset + row.size) return null
    return row.contents[x - row.offset]
}

private fun List<FieldRow>.splitByRectangles(verticalLines: List<Int>, horizontalLines: List<Int>) = sequence {
    for (i in 0 until verticalLines.size - 1) {
        for (j in 0 until horizontalLines.size - 1) {
            val rect =                         SweepRectangle(
                verticalLines[i] until verticalLines[i + 1],
                horizontalLines[j] until horizontalLines[j + 1]
            )
            if (rect.isFilled(this@splitByRectangles)) yield(rect)
        }
    }
}.toList()


data class SweepGraphEdge(val from: SweepRectangle, val to: SweepRectangle, val type: SweepConnectionType) {
    val sector: SweepSection
        get() = when (type) {
            SweepConnectionType.TOP -> from.topSide
            SweepConnectionType.BOTTOM -> from.belowBottomSide
            SweepConnectionType.LEFT -> from.leftSide
            SweepConnectionType.RIGHT -> from.rightOfRightSide
        }
}

class SweepGraph(rectangles: Iterable<SweepRectangle>, bent: Boolean = false) {
    val verticles = rectangles.toSet()
    val edges = verticles.edges()
        .groupBy { it.from }
        .asSequence()
        .map { (k, v) -> k to v.associateBy { it.to } }
        .toMap()

    fun getEdgesFrom(from: SweepRectangle) = edges[from] ?: mapOf()

    fun getEdge(from: SweepRectangle, to: SweepRectangle) = getEdgesFrom(from)[to]

    fun bend(): SweepGraph {
        val reference =
            edges.asSequence().filter { (k, v) ->
                when {
                    v.size >= 3 -> true
                    v.size == 2 -> {
                        val vList = v.values.toList()
                        val first = vList[0]
                        val second = vList[1]
                        !first.type.isOppositeTo(second.type)
                    }
                    else -> false
                }
            }.first().key
        val w = reference.width
        val h = reference.height
        val d = getEdgesFrom(reference).values.minOf {
            when (it.type) {
                SweepConnectionType.TOP, SweepConnectionType.BOTTOM -> it.to.height
                SweepConnectionType.RIGHT, SweepConnectionType.LEFT -> it.to.width
            }
        }

        val rects = sequence {
            val stack = ArrayDeque<ToVisit>()
            val visited = mutableSetOf<SweepRectangle>()

            stack.addLast(ToVisit(reference, RollingCube(w, h, d)))

            while (stack.isNotEmpty()) {
                val current = stack.removeLast()

                visited.add(current.rectangle)

                var cube = current.rollingCube

                when {
                    current.direction == null -> {
                        check (cube.currentH == current.rectangle.height &&
                                cube.currentW == current.rectangle.width)
                        yield(current.rectangle)
                    }
                    cube.currentW == current.rectangle.width &&
                            cube.currentH == current.rectangle.height -> {
                        yield(current.rectangle)
                    }
                    cube.currentW == current.rectangle.width -> {
                        val holder = CubeHolder(cube)
                        when (current.direction) {
                            SweepConnectionType.TOP -> yieldAll(current.rectangle.bendTop(holder))
                            SweepConnectionType.BOTTOM -> yieldAll(current.rectangle.bendBottom(holder))
                            else -> throw IllegalStateException("Should not happen")
                        }
                        cube = holder.cube
                    }
                    cube.currentH == current.rectangle.height -> {
                        val holder = CubeHolder(cube)
                        when (current.direction) {
                            SweepConnectionType.LEFT -> yieldAll(current.rectangle.bendLeft(holder))
                            SweepConnectionType.RIGHT -> yieldAll(current.rectangle.bendRight(holder))
                            else -> throw IllegalStateException("Should not happen")
                        }
                        cube = holder.cube
                    }
                    else -> throw IllegalStateException("Should not happen")
                }

                for ((_, edge) in getEdgesFrom(current.rectangle)) {
                    if (edge.to in visited)
                        continue

                    val newCube = when (edge.type) {
                        SweepConnectionType.LEFT -> cube.rollLeft()
                        SweepConnectionType.RIGHT -> cube.rollRight()
                        SweepConnectionType.TOP -> cube.rollTop()
                        SweepConnectionType.BOTTOM -> cube.rollBottom()
                    }

                    stack.addLast(ToVisit(edge.to, newCube, edge.type))
                }
            }
        }

        return SweepGraph(rects.asIterable(), true)
    }

    private data class CubeHolder(var cube: RollingCube)

    private fun SweepRectangle.bendBottom(cubeHolder: CubeHolder) = sequence {
        var ctr = 0

        var currentY = yRange.first
        var currentCube = cubeHolder.cube
        while (currentY < yRange.last + 1) {
            yield(
                SweepRectangle(
                    xRange,
                    currentY until (currentY + currentCube.currentH)
                )
            )
            ctr++
            currentY += currentCube.currentH
            currentCube = currentCube.rollBottom()
        }

        check(ctr <= 4)
        check(currentY == yRange.last + 1)
        cubeHolder.cube = currentCube
    }

    private fun SweepRectangle.bendTop(cubeHolder: CubeHolder) = sequence {
        var ctr = 0

        var currentY = yRange.last + 1
        var currentCube = cubeHolder.cube
        while (currentY > yRange.first) {
            yield(
                SweepRectangle(
                    xRange,
                    currentY - currentCube.currentH until currentY
                )
            )
            ctr++
            currentY -= currentCube.currentH
            currentCube = currentCube.rollTop()
        }

        check(ctr <= 4)
        check(currentY == yRange.first)
        cubeHolder.cube = currentCube
    }

    private fun SweepRectangle.bendRight(cubeHolder: CubeHolder) = sequence {
        var ctr = 0

        var currentX = xRange.first
        var currentCube = cubeHolder.cube
        while (currentX < xRange.last + 1) {
            yield(
                SweepRectangle(
                    currentX until currentX + currentCube.currentW,
                    yRange
                )
            )
            ctr++
            currentX += currentCube.currentW
            currentCube = currentCube.rollRight()
        }

        check(ctr <= 4)
        check(currentX == xRange.last + 1)
        cubeHolder.cube = currentCube
    }

    private fun SweepRectangle.bendLeft(cubeHolder: CubeHolder) = sequence {
        var ctr = 0

        var currentX = xRange.last + 1
        var currentCube = cubeHolder.cube
        while (currentX > xRange.first) {
            yield(
                SweepRectangle(
                    currentX - currentCube.currentW until currentX,
                    yRange
                )
            )
            ctr++
            currentX -= currentCube.currentW
            currentCube = currentCube.rollLeft()
        }

        check(ctr <= 4)
        check(currentX == xRange.first)
        cubeHolder.cube = currentCube
    }

    private data class ToVisit(val rectangle: SweepRectangle,
                               val rollingCube: RollingCube,
                               val direction: SweepConnectionType? = null
    )

    private data class RollingCube(val w: Int,
                                   val h: Int,
                                   val d: Int,
                                   val mode: Mode = Mode.XY
    ) {
        enum class Mode {
            XZ {
                override fun rollVertically(): Mode = XY
                override fun rollHorizontally(): Mode = YZ
            },
            XY {
                override fun rollVertically(): Mode = XZ
                override fun rollHorizontally(): Mode = ZY
            },
            ZX {
                override fun rollVertically(): Mode = ZY
                override fun rollHorizontally(): Mode = YX

            },
            ZY {
                override fun rollVertically(): Mode = ZX
                override fun rollHorizontally(): Mode = XY
            },
            YX {
                override fun rollVertically(): Mode = YZ
                override fun rollHorizontally(): Mode = ZX
            },
            YZ {
                override fun rollVertically(): Mode = YX
                override fun rollHorizontally(): Mode = XZ
            };

            abstract fun rollVertically(): Mode
            abstract fun rollHorizontally(): Mode
        }

        val currentW = w.absoluteValue
        val currentH= h.absoluteValue
        val transposed = mode == Mode.ZX || mode == Mode.YX || mode == Mode.ZY


        fun rollTop() = RollingCube(w, -d, h, mode.rollVertically())
        fun rollBottom() = RollingCube(w, d, -h, mode.rollVertically())
        fun rollLeft() = RollingCube(-d, h, w, mode.rollHorizontally())
        fun rollRight() = RollingCube(d, h, -w, mode.rollHorizontally())

        val xDirectionIncreasing = w > 0
        val yDirectionIncreasing = h > 0
    }

    fun toCube(startPoint: Coordinates): Cube {

        val start = verticles.first {
            it.xRange.first == startPoint.x && it.yRange.first == startPoint.y
        }
        val w = start.width
        val h = start.height

        val neighbor = getEdgesFrom(start).values.first()
        val d = when (neighbor.type) {
            SweepConnectionType.TOP, SweepConnectionType.BOTTOM -> neighbor.to.height
            SweepConnectionType.LEFT, SweepConnectionType.RIGHT -> neighbor.to.width
        }

        val rollingCube = RollingCube(w, h, d)

        val stack = ArrayDeque<ToVisitCube>()
        stack.addLast(ToVisitCube(start, FacetType.BOTTOM, rollingCube))

        val map = mutableMapOf<FacetType, CubeFacet>()
        val visited = mutableSetOf<SweepRectangle>()


        while (stack.isNotEmpty()) {
            val current = stack.removeLast()

            visited.add(current.rect)

            val cube = current.rollingCube

            check(current.type !in map)

            map[current.type] = CubeFacet(
                type = current.type,
                sweepStartX = if (cube.xDirectionIncreasing) current.rect.xRange.first else current.rect.xRange.last,
                sweepEndInclusiveX = if (cube.xDirectionIncreasing) current.rect.xRange.last else current.rect.xRange.first,
                sweepStartY = if (cube.yDirectionIncreasing) current.rect.yRange.first else current.rect.yRange.last,
                sweepEndInclusiveY = if (cube.yDirectionIncreasing) current.rect.yRange.last else current.rect.yRange.first,
                transposed = cube.transposed
            )

            for ((_, edge) in getEdgesFrom(current.rect)) {
                if (edge.to in visited) continue
                val realDir = edge.type.realDir(
                    cube.transposed,
                    if (cube.xDirectionIncreasing) 1 else -1,
                    if (cube.yDirectionIncreasing) 1 else -1
                )
                stack.addLast(
                    ToVisitCube(
                        edge.to,
                        current.type.neighbor(realDir),
                        when (edge.type) {
                            SweepConnectionType.LEFT -> cube.rollLeft()
                            SweepConnectionType.RIGHT -> cube.rollRight()
                            SweepConnectionType.TOP -> cube.rollTop()
                            SweepConnectionType.BOTTOM -> cube.rollBottom()
                        }
                    ),
                )
            }
        }

        return Cube(map)
    }

    private data class ToVisitCube(val rect: SweepRectangle, val type: FacetType, val rollingCube: RollingCube)

    companion object {
        private fun Set<SweepRectangle>.edges(): Sequence<SweepGraphEdge> {
            val sides =
                mutableMapOf<SweepSection, MutableList<Pair<SweepRectangle, SweepConnectionType>>>()

            for (rect in this) {
                sides[rect.leftSide] = (sides[rect.leftSide] ?: mutableListOf()).apply {
                    add(rect to SweepConnectionType.LEFT)
                }
                sides[rect.rightOfRightSide] = (sides[rect.rightOfRightSide] ?: mutableListOf()).apply {
                    add(rect to SweepConnectionType.RIGHT)
                }
                sides[rect.topSide] = (sides[rect.topSide] ?: mutableListOf()).apply {
                    add(rect to SweepConnectionType.TOP)
                }
                sides[rect.belowBottomSide] = (sides[rect.belowBottomSide] ?: mutableListOf()).apply {
                    add(rect to SweepConnectionType.BOTTOM)
                }
            }

            require(sides.values.all { it.size <= 2 })
            val shared = sides.filter { it.value.size == 2 }

            return sequence {
                for ((_, joined) in shared) {
                    val (rect1, conn1) = joined.first()
                    val (rect2, conn2) = joined[1]

                    check(conn1.isOppositeTo(conn2))

                    yield(SweepGraphEdge(rect1, rect2, conn1))
                    yield(SweepGraphEdge(rect2, rect1, conn2))
                }
            }
        }
    }
}

class Field(private val rows: List<FieldRow>, private val instructions: List<Instruction>) {
    val cube: Cube
    var coordinates = Coordinates3D(0, 0, -1)
        private set
    var direction = Direction.RIGHT
        private set
    val flatDirection
        get() = cube.facets[currentFacetType]!!.correctFlatDirection(direction.flatDirection(currentFacetType))

    init {
        require(rows.isNotEmpty())

        val verticalLines = rows.getVerticalLines()
        val horizontalLines = rows.getHorizontalLines()

        val (startY, startRow) = rows.asSequence().withIndex()
            .first { (_, it) -> it.size != 0 }

        val startX = startRow.offset

        cube = SweepGraph(rows.splitByRectangles(verticalLines, horizontalLines))
            .bend()
            .toCube(Coordinates(startX, startY))
    }


    fun runInstructions() {
        for (instr in instructions) {
            when (instr) {
                is Go ->{
                    go(instr.amount)
                }
                Rotate.CW -> {
                    direction = direction.cw(currentFacetType)
                }
                Rotate.CCW -> {
                    direction = direction.ccw(currentFacetType)
                }
            }
        }
    }

    private fun facetType(coordinates: Coordinates3D) = when {
        coordinates.z == -1 -> FacetType.BOTTOM
        coordinates.z == cube.height -> FacetType.TOP
        coordinates.y == -1 -> FacetType.BACK
        coordinates.y == cube.depth -> FacetType.FRONT
        coordinates.x == -1 -> FacetType.LEFT
        coordinates.x == cube.width -> FacetType.RIGHT
        else -> throw IllegalStateException("$coordinates are not on a facet")
    }

    private val currentFacetType: FacetType
        get() = facetType(coordinates)

    private fun go(amount: Int) {
        for (i in 0 until amount) {
            if (!step()) break
        }
    }

    private fun step(): Boolean {
        val delta = when (direction) {
            Direction.UP -> Coordinates3D(0, 0, 1)
            Direction.DOWN -> Coordinates3D(0, 0, -1)
            Direction.LEFT -> Coordinates3D(-1, 0, 0)
            Direction.RIGHT -> Coordinates3D(1, 0, 0)
            Direction.BACK -> Coordinates3D(0, -1, 0)
            Direction.FRONT -> Coordinates3D(0, 1, 0)
        }

        val newCoords = coordinates + delta
        val (newCoordsWrapped, newDirection) = newCoords.wrap()
        val sweepCoordinates = sweepCoords(newCoordsWrapped)
        val row = rows[sweepCoordinates.y]
        val value = row.contents[sweepCoordinates.x - row.offset]

        return if (!value) {
            coordinates = newCoordsWrapped
            direction = newDirection
            true
        } else {
            false
        }
    }

    private fun sweepCoords(coordinates: Coordinates3D): Coordinates {
        val facetType = facetType(coordinates)
        val facet = cube.facets[facetType]!!

        val facetCoords = when (facetType) {
            FacetType.TOP, FacetType.BOTTOM -> Coordinates(coordinates.x, coordinates.y)
            FacetType.LEFT, FacetType.RIGHT -> Coordinates(coordinates.y, coordinates.z)
            FacetType.BACK, FacetType.FRONT -> Coordinates(coordinates.x, coordinates.z)
        }

        return facet.coordinatesOnSweep(facetCoords.x, facetCoords.y)
    }

    val sweepCoords: Coordinates
        get() = sweepCoords(coordinates)


    private fun Coordinates3D.wrap(): Pair<Coordinates3D, Direction> {
        val facet = currentFacetType

        // Well, fuck...
        return when {
            facet == FacetType.BOTTOM && this.x == -1 -> Coordinates3D(-1, y, 0) to Direction.UP
            facet == FacetType.BOTTOM && this.x == cube.width -> Coordinates3D(cube.width, y, 0) to Direction.UP
            facet == FacetType.BOTTOM && this.y == -1 -> Coordinates3D(x, -1, 0) to Direction.UP
            facet == FacetType.BOTTOM && this.y == cube.depth -> Coordinates3D(x, cube.depth, 0) to Direction.UP

            facet == FacetType.TOP && this.x == -1 -> Coordinates3D(-1, y, cube.height - 1) to Direction.DOWN
            facet == FacetType.TOP && this.x == cube.width -> Coordinates3D(cube.width, y, cube.height - 1) to Direction.DOWN
            facet == FacetType.TOP && this.y == -1 -> Coordinates3D(x, -1, cube.height - 1) to Direction.DOWN
            facet == FacetType.TOP && this.y == cube.depth -> Coordinates3D(x, cube.depth, cube.height - 1) to Direction.DOWN

            facet == FacetType.FRONT && this.x == -1 -> Coordinates3D(-1, cube.depth - 1, z) to Direction.BACK
            facet == FacetType.FRONT && this.x == cube.width -> Coordinates3D(cube.width, cube.depth - 1, z) to Direction.BACK
            facet == FacetType.FRONT && this.z == -1 -> Coordinates3D(x, cube.depth - 1, -1) to Direction.BACK
            facet == FacetType.FRONT && this.z == cube.height -> Coordinates3D(x, cube.depth - 1, cube.height) to Direction.BACK

            facet == FacetType.BACK && this.x == -1 -> Coordinates3D(-1, 0, z) to Direction.FRONT
            facet == FacetType.BACK && this.x == cube.width -> Coordinates3D(cube.width, 0, z) to Direction.FRONT
            facet == FacetType.BACK && this.z == -1 -> Coordinates3D(x, 0, -1) to Direction.FRONT
            facet == FacetType.BACK && this.z == cube.height -> Coordinates3D(x, 0, cube.height) to Direction.FRONT

            facet == FacetType.LEFT && this.y == -1 -> Coordinates3D(0, -1, z) to Direction.RIGHT
            facet == FacetType.LEFT && this.y == cube.depth -> Coordinates3D(0, cube.depth, z) to Direction.RIGHT
            facet == FacetType.LEFT && this.z == -1 -> Coordinates3D(0, y, -1) to Direction.RIGHT
            facet == FacetType.LEFT && this.z == cube.height -> Coordinates3D(0, y, cube.height) to Direction.RIGHT

            facet == FacetType.RIGHT && this.y == -1 -> Coordinates3D(cube.width - 1, -1, z) to Direction.LEFT
            facet == FacetType.RIGHT && this.y == cube.depth -> Coordinates3D(cube.width - 1, cube.depth, z) to Direction.LEFT
            facet == FacetType.RIGHT && this.z == -1 -> Coordinates3D(cube.width - 1, y, -1) to Direction.LEFT
            facet == FacetType.RIGHT && this.z == cube.height -> Coordinates3D(cube.width - 1, y, cube.height) to Direction.LEFT
            else -> this to direction

        }
    }

}

enum class FlatDirection {
    UP {
        override val cw: FlatDirection
            get() = RIGHT
        override val ccw: FlatDirection
            get() = LEFT
        override val opposite: FlatDirection
            get() = DOWN
    },
    DOWN {
        override val cw: FlatDirection
            get() = LEFT
        override val ccw: FlatDirection
            get() = RIGHT
        override val opposite: FlatDirection
            get() = UP
    },
    LEFT {
        override val cw: FlatDirection
            get() = UP
        override val ccw: FlatDirection
            get() = DOWN
        override val opposite: FlatDirection
            get() = RIGHT
    },
    RIGHT {
        override val cw: FlatDirection
            get() = DOWN
        override val ccw: FlatDirection
            get() = UP
        override val opposite: FlatDirection
            get() = LEFT
    };

    abstract val cw: FlatDirection
    abstract val ccw: FlatDirection
    abstract val opposite: FlatDirection
}


enum class Direction {
    UP {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> LEFT
            FacetType.BACK -> RIGHT
            FacetType.RIGHT -> FRONT
            FacetType.LEFT -> BACK
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> RIGHT
            FacetType.BACK -> LEFT
            FacetType.RIGHT -> BACK
            FacetType.LEFT -> FRONT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.FRONT -> FlatDirection.DOWN
            FacetType.BACK -> FlatDirection.DOWN
            FacetType.RIGHT -> FlatDirection.DOWN
            FacetType.LEFT -> FlatDirection.DOWN
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

    },
    DOWN {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> RIGHT
            FacetType.BACK -> LEFT
            FacetType.RIGHT -> BACK
            FacetType.LEFT -> FRONT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> LEFT
            FacetType.BACK -> RIGHT
            FacetType.RIGHT -> FRONT
            FacetType.LEFT -> BACK
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.FRONT -> FlatDirection.UP
            FacetType.BACK -> FlatDirection.UP
            FacetType.RIGHT -> FlatDirection.UP
            FacetType.LEFT -> FlatDirection.UP
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }
    },
    LEFT {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> DOWN
            FacetType.BACK -> UP
            FacetType.TOP -> FRONT
            FacetType.BOTTOM -> BACK
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> UP
            FacetType.BACK -> DOWN
            FacetType.TOP -> BACK
            FacetType.BOTTOM -> FRONT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.FRONT -> FlatDirection.LEFT
            FacetType.BACK -> FlatDirection.LEFT
            FacetType.TOP -> FlatDirection.LEFT
            FacetType.BOTTOM -> FlatDirection.LEFT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }
    },
    RIGHT {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> UP
            FacetType.BACK -> DOWN
            FacetType.TOP -> BACK
            FacetType.BOTTOM -> FRONT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.FRONT -> DOWN
            FacetType.BACK -> UP
            FacetType.TOP -> FRONT
            FacetType.BOTTOM  -> BACK
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.FRONT -> FlatDirection.RIGHT
            FacetType.BACK -> FlatDirection.RIGHT
            FacetType.TOP -> FlatDirection.RIGHT
            FacetType.BOTTOM -> FlatDirection.RIGHT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }
    },
    FRONT {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.LEFT -> UP
            FacetType.RIGHT -> DOWN
            FacetType.TOP -> RIGHT
            FacetType.BOTTOM -> LEFT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.LEFT -> DOWN
            FacetType.RIGHT -> UP
            FacetType.TOP -> LEFT
            FacetType.BOTTOM -> RIGHT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.LEFT -> FlatDirection.RIGHT
            FacetType.RIGHT -> FlatDirection.RIGHT
            FacetType.TOP -> FlatDirection.DOWN
            FacetType.BOTTOM -> FlatDirection.DOWN
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }
    },
    BACK {
        override fun cw(facet: FacetType) = when (facet) {
            FacetType.LEFT -> DOWN
            FacetType.RIGHT -> UP
            FacetType.TOP -> LEFT
            FacetType.BOTTOM -> RIGHT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun ccw(facet: FacetType) = when (facet) {
            FacetType.LEFT -> UP
            FacetType.RIGHT -> DOWN
            FacetType.TOP -> RIGHT
            FacetType.BOTTOM -> LEFT
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }

        override fun flatDirection(facet: FacetType) = when (facet) {
            FacetType.LEFT -> FlatDirection.LEFT
            FacetType.RIGHT -> FlatDirection.LEFT
            FacetType.TOP -> FlatDirection.UP
            FacetType.BOTTOM -> FlatDirection.UP
            else -> throw IllegalStateException("You cannot go $this on facet $facet")
        }
    };

    abstract fun cw(facet: FacetType): Direction
    abstract fun ccw(facet: FacetType): Direction
    abstract fun flatDirection(facet: FacetType): FlatDirection
}

sealed interface Instruction

data class Go(val amount: Int) : Instruction

enum class Rotate : Instruction {
    CW, CCW
}

object MonkeyFieldParser {
    private const val SPACE = ' '
    private const val DOT = '.'
    private const val HASH = '#'
    private const val NL = '\n'
    private const val NL_WIN = "\r\n"
    private const val L = 'L'
    private const val R = 'R'

    private val NEWLINE = Parsers.or(
        Scanners.isChar(NL),
        Scanners.string(NL_WIN)
    )

    private val SPACES = Scanners.isChar(SPACE).many().map { it.size }

    private val DOT_PARSER = Scanners.isChar(DOT).map { false }
    private val HASH_PARSER = Scanners.isChar(HASH).map { true }

    private val ROW_CONTENTS = Parsers.or(
        DOT_PARSER, HASH_PARSER
    ).many1().map {
        val res = BitSet()
        for ((ix, bool) in it.withIndex()) {
            res.set(ix, bool)
        }
        res to it.size
    }

    private val ROW = Parsers.sequence(
        SPACES,
        ROW_CONTENTS,
    ) { offset, (content, length) ->
        FieldRow(offset, length, content)
    }
    private val ROWS = ROW.sepBy(
        NEWLINE
    )

    private val L_PARSER = Scanners.isChar(L).map { Rotate.CCW }
    private val R_PARSER = Scanners.isChar(R).map { Rotate.CW }
    private val GO_PARSER = Scanners.INTEGER.map { Go(it.toInt()) }
    private val COMMAND = Parsers.or(
        L_PARSER,
        R_PARSER,
        GO_PARSER
    )
    private val COMMANDS = COMMAND.many()

    private val FIELD = Parsers.sequence(
        ROWS,
        NEWLINE,
        NEWLINE,
        COMMANDS,
        NEWLINE.many()
    ) { rows, _, _, cmds, _ ->
        Field(rows, cmds)
    }

    fun parse(input: String) = FIELD.parse(input)
}

fun evaluate(input: String): Int {
    val field = MonkeyFieldParser.parse(input)
    field.runInstructions()

    return (field.sweepCoords.y + 1) * 1000 + (field.sweepCoords.x + 1) * 4 + + when (field.flatDirection) {
        FlatDirection.RIGHT -> 0
        FlatDirection.DOWN -> 1
        FlatDirection.LEFT -> 2
        FlatDirection.UP -> 3
    }
}


fun day22Part2() {
    println("day22/part2/field: \n${
        evaluate(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day22/field.txt").bufferedReader().readText()
        )
    }")
}
