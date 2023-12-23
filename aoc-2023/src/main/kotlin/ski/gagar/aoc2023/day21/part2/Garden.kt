package ski.gagar.aoc2023.day21.part2

import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

interface HasXy<Self : HasXy<Self>> {
    val x: Int
    val y: Int

    fun neighbors() = sequence {
        yield(makeNeighbor(x - 1, y))
        yield(makeNeighbor(x + 1, y))
        yield(makeNeighbor(x, y - 1))
        yield(makeNeighbor(x, y + 1))
    }

    fun relationTo(other: Self) = when {
        this.y == other.y && this.x == other.x - 1 -> Direction.LEFT
        this.y == other.y && this.x == other.x + 1 -> Direction.RIGHT
        this.y == other.y - 1 && this.x == other.x -> Direction.UP
        this.y == other.y + 1 && this.x == other.x -> Direction.DOWN
        else -> null
    }

    fun relativeTo(other: Self) = makeNeighbor(x - other.x, y - other.y)

    operator fun plus(other: Self) = makeNeighbor(x + other.x, y + other.y)

    fun makeNeighbor(x: Int, y: Int): Self
}

data class InTileCoordinates(override val x: Int, override val y: Int) : HasXy<InTileCoordinates> {
    override fun makeNeighbor(x: Int, y: Int) = InTileCoordinates(x, y)
}

data class TileCoordinates(override val x: Int, override val y: Int): HasXy<TileCoordinates> {
    override fun makeNeighbor(x: Int, y: Int): TileCoordinates = TileCoordinates(x, y)

    companion object {
        val ZERO = TileCoordinates(0, 0)
    }
}

data class GlobalCoordinates(override val x: Int, override val y: Int): HasXy<GlobalCoordinates> {
    override fun makeNeighbor(x: Int, y: Int): GlobalCoordinates = GlobalCoordinates(x, y)
}

data class TileAndLocal(val tile: TileCoordinates, val inTile: InTileCoordinates) {
    fun relativeTo(rel: TileCoordinates) = TileAndLocal(tile.relativeTo(rel), inTile)
}

typealias InTileCoverage = Set<InTileCoordinates>

class Garden(val rocks: Set<InTileCoordinates>, val width: Int, val height: Int) {

    private val TileAndLocal.global
        get() = GlobalCoordinates(tile.x * width + inTile.x, tile.y * height + inTile.y)
    private val GlobalCoordinates.tile
        get() = TileCoordinates(Math.floorDiv(x, width), Math.floorDiv(y, height))
    private val GlobalCoordinates.inTile
        get() = InTileCoordinates(Math.floorMod(x, width), Math.floorMod(y, height))
    private val GlobalCoordinates.local
        get() = TileAndLocal(tile, inTile)
    private val InTileCoordinates.isNormalized
        get() = x in 0 until width && y in 0 until height
    private val TileAndLocal.isNormalized
        get() = inTile.isNormalized
    private val TileAndLocal.normalized
        get() = global.local

    private data class TileCoverage(
        val coordinates: Set<InTileCoordinates>
    )

    private class CoveragePerIter(val start: Int) {
        private val map: MutableMap<Int, LayerAndOutskirt> = mutableMapOf()

        val odd: LayerAndOutskirt?
            get() = map[0]

        val even: LayerAndOutskirt?
            get() = map[1]

        val isComplete: Boolean
            get() = odd != null && even != null

        fun register(iter: Int, tileCoverage: LayerAndOutskirt) {
            val key = iter % 2

            check(map[key] == null)

            map[key] = tileCoverage
        }

        fun isLayerEquivalent(other: CoveragePerIter) =
            this.isComplete && other.isComplete && this.odd!!.layer == other.odd!!.layer && this.even!!.layer == other.even!!.layer

        fun isFullyEquivalent(other: CoveragePerIter) =
            this.isComplete && other.isComplete && this.odd == other.odd && this.even == other.even
        fun isFullyCounterEquivalent(other: CoveragePerIter) =
            this.isComplete && other.isComplete && this.odd == other.even && this.even == other.odd
    }

    interface LayerFootprint {
        val equalized: InTileCoverage?
        val empty: Boolean

        fun sizeAtLayer(n: Int): BigInteger
        fun brief(): String

        companion object {
            fun from(byTile: Map<TileCoordinates, InTileCoverage>, n: Int) =
                when (n) {
                    0 -> l0(byTile)
                    1 -> l1(byTile)
                    else -> l2Plus(byTile, n)
                }

            private fun l0(byTile: Map<TileCoordinates, InTileCoverage>) =
                Layer0FootPrint(byTile[TileCoordinates(0, 0)] ?: setOf())

            private fun l1(byTile: Map<TileCoordinates, InTileCoverage>) =
                Layer1FootPrint(
                    t = byTile[TileCoordinates(0, -1)] ?: setOf(),
                    r = byTile[TileCoordinates(1, 0)] ?: setOf(),
                    b = byTile[TileCoordinates(0, 1)] ?: setOf(),
                    l = byTile[TileCoordinates(-1, 0)] ?: setOf(),
                )

            private fun l2Plus(byTile: Map<TileCoordinates, InTileCoverage>, n: Int): Layer2PlusFootPrint? {
                require(n >= 2)
                val t = byTile[TileCoordinates(0, -n)] ?: setOf()
                val r = byTile[TileCoordinates(n, 0)] ?: setOf()
                val b = byTile[TileCoordinates(0, n)] ?: setOf()
                val l = byTile[TileCoordinates(-n, 0)] ?: setOf()

                val allTr = (1..<n).map { byTile[TileCoordinates(it, -(n - it))] ?: setOf() }
                if (!allTr.all { it == allTr.first() })
                    return null
                val allBr = (1..<n).map { byTile[TileCoordinates(it, (n - it))] ?: setOf() }
                if (!allBr.all { it == allBr.first() })
                    return null
                val allBl = (1..<n).map { byTile[TileCoordinates(-it, (n - it))] ?: setOf() }
                if (!allBl.all { it == allBl.first() })
                    return null
                val allTl = (1..<n).map { byTile[TileCoordinates(-it, -(n - it))] ?: setOf() }
                if (!allTl.all { it == allTl.first() })
                    return null

                return Layer2PlusFootPrint(
                    t = t,
                    r = r,
                    b = b,
                    l = l,
                    tr = allTr.first(),
                    br = allBr.first(),
                    bl = allBl.first(),
                    tl = allTl.first()
                )
            }

        }

    }

    data class Layer0FootPrint(val me: InTileCoverage): LayerFootprint {
        override val equalized: InTileCoverage = me
        override val empty
            get() = me.isEmpty()

        override fun brief(): String = "[L0: ${me.size}]"

        override fun sizeAtLayer(n: Int): BigInteger {
            check(n == 0)
            return me.size.toBigInteger()
        }
    }

    data class Layer1FootPrint(
        val t: InTileCoverage,
        val r: InTileCoverage,
        val b: InTileCoverage,
        val l: InTileCoverage
    ) : LayerFootprint {
        private val all
            get() = sequenceOf(t, r, b, l)

        override val equalized: InTileCoverage?
            get() {
                if (!all.all { it == t })
                    return null
                return t
            }

        override val empty: Boolean
            get() = all.all { it.isEmpty() }

        override fun sizeAtLayer(n: Int): BigInteger {
            check(n == 1)
            return (t.size + r.size + b.size + l.size).toBigInteger()
        }

        override fun brief(): String =
            "[L1: ${all.map { it.size }.joinToString(" ")}]"

    }

    data class Layer2PlusFootPrint(
        val t: InTileCoverage,
        val tr: InTileCoverage,
        val r: InTileCoverage,
        val br: InTileCoverage,
        val b: InTileCoverage,
        val bl: InTileCoverage,
        val l: InTileCoverage,
        val tl: InTileCoverage
    ) : LayerFootprint {
        private val all
            get() = sequenceOf(t, tr, r, br, b, bl, l, tl)

        override val equalized: InTileCoverage?
            get() {
                if (!all.all { it == t })
                    return null
                return t
            }
        override val empty: Boolean
            get() = all.all { it.isEmpty() }

        override fun sizeAtLayer(n: Int): BigInteger {
            check(n >= 2)
            val constant = (t.size + r.size + b.size + l.size).toBigInteger()
            val linear = (tr.size + br.size + bl.size + tl.size).toBigInteger() * (n - 1).toBigInteger()
            return constant + linear
        }
        override fun brief(): String = "[L2: ${all.map { it.size }.joinToString (" ")}}]"

    }

    data class LayerAndOutskirt(
        val layer: LayerFootprint,
        val outskirts: List<LayerFootprint>
    ) {
        val equalized = layer.equalized != null

        val shifted by lazy {
            require(outskirts.isNotEmpty())
            LayerAndOutskirt(outskirts.first(), outskirts.drop(1))
        }

        fun brief() =
            "Layer: ${layer.brief()}, Outskirts: ${outskirts.map { it.brief() }.joinToString(" ")}"
    }

    private fun Set<GlobalCoordinates>.tileCoverage(n: Int): LayerAndOutskirt? {
        val byTile =
            this.groupBy { it.tile }.mapValues { (_, v) -> v.asSequence().map { it.inTile }.toSet() }

        val layer = LayerFootprint.from(byTile, n) ?: return null

        val outskirts = mutableListOf<LayerFootprint>()
        var os = n + 1
        while (true) {
            val outskirt = LayerFootprint.from(byTile, os) ?: return null
            if (outskirt.empty)
                break
            outskirts.add(outskirt)
            os++
        }


        return LayerAndOutskirt(layer, outskirts)
    }

    class FootPrintTracker {
        private val map: MutableMap<LayerAndOutskirt, LayerAndOutskirt> = mutableMapOf()

        fun register(from: LayerAndOutskirt, to: LayerAndOutskirt) {
            map[from] = to
        }

        fun clear() {
            map.clear()
        }

        fun replay(from: LayerAndOutskirt, steps: Int): LayerAndOutskirt {
            var cur = from
            for (i in 0 until  steps) {
                cur = map[cur]!!
            }
            return cur
        }
    }


    fun coveredCoords(from: TileAndLocal, steps: Int, naive: Boolean): BigInteger {
        val visited = mutableSetOf<GlobalCoordinates>()
        val queue = ArrayDeque<QueueItem>()
        queue.add(QueueItem(from.global))
        var currentLayer = 0
        var currentTileLayer = 0

        val layerCoverages = TreeMap<Int, CoveragePerIter>()
        val coveragesPerStart = mutableMapOf<Int, LayerAndOutskirt>()
        val footprintTracker = FootPrintTracker()
        var cov: LayerAndOutskirt? = null
        var covBefore: LayerAndOutskirt?
        var shouldShift = false
        var oneMoreRequired = true
        while (queue.isNotEmpty()) {
            val (xy, layer) = queue.removeFirst()
            if (layer > steps) break
            if (layer > currentLayer) {
                currentLayer = layer
                covBefore = cov

                if (shouldShift) {
                    covBefore = covBefore!!.shifted
                    shouldShift = false
                }

                cov = visited.tileCoverage(currentTileLayer)

                if (covBefore != null) {
                    check(cov != null)
                }

                if (null != covBefore && null != cov) {
                    footprintTracker.register(covBefore, cov)
                }

                coveragesPerStart.keys.removeIf { it < layer - 4 }
                if (cov != null)
                    coveragesPerStart[layer] = cov

                if (cov?.equalized == true) {
                    val prevCpi = CoveragePerIter(layer - 3)
                    coveragesPerStart[layer - 3]?.let {
                        prevCpi.register(layer - 3, it)
                    }
                    coveragesPerStart[layer - 2]?.let {
                        prevCpi.register(layer - 2, it)
                    }

                    val currentCpi = CoveragePerIter(layer - 1)
                    coveragesPerStart[layer - 1]?.let {
                        currentCpi.register(layer - 1, it)
                    }
                    coveragesPerStart[layer - 2]?.let {
                        currentCpi.register(layer, it)
                    }

                    if (currentCpi.isLayerEquivalent(prevCpi)) {
                        layerCoverages[currentTileLayer] = prevCpi

                        footprintTracker.register(
                            coveragesPerStart[layer - 3]!!.shifted,
                            coveragesPerStart[layer - 2]!!.shifted
                        )
                        footprintTracker.register(
                            coveragesPerStart[layer - 2]!!.shifted,
                            coveragesPerStart[layer - 1]!!.shifted
                        )
                        footprintTracker.register(
                            coveragesPerStart[layer - 1]!!.shifted,
                            coveragesPerStart[layer]!!.shifted
                        )
                        shouldShift = true

                        val thisLayer = prevCpi
                        val prevLayer = layerCoverages[currentTileLayer - 1]
                        if (!naive && (prevLayer?.isFullyEquivalent(thisLayer) == true || prevLayer?.isFullyCounterEquivalent(thisLayer) == true)) {
                                break
                        }
                        currentTileLayer++
                    }
                }
                visited.clear()

            }
            if (xy in visited) continue
            visited.add(xy)

            for (n in xy.neighbors()) {
                if (n.inTile !in rocks) queue.add(QueueItem(n, layer + 1))
            }
        }

        require(currentLayer <= steps)

        if (currentLayer == steps) {
            return visited.size.toBigInteger()
        }

        // Need to extrapolate
        val lastLayer = layerCoverages.lastKey()
        val prevLayer = lastLayer - 1
        val lastCov = layerCoverages[lastLayer] ?: throw IllegalStateException("failed to extrapolate")
        val prevCov = layerCoverages[prevLayer] ?: throw IllegalStateException("failed to extrapolate")

        var count: BigInteger = BigInteger.ZERO
        for ((k, v) in layerCoverages) {
            val nTiles = if (k == 0) 1 else 4 * k
            val tileFilled = if (steps % 2 == 0) v.even!!.layer.equalized!!.size else v.odd!!.layer.equalized!!.size
            count += tileFilled.toBigInteger() * nTiles.toBigInteger()
        }

        val diff = lastCov.start - prevCov.start
        val stepsToSkip = (steps - lastCov.start)


        val layersToExtrapolate = stepsToSkip / diff
        val remAfterExtra = stepsToSkip % diff
        require(lastLayer > 0)

        val doubleLayers = layersToExtrapolate / 2
        val remDouble = layersToExtrapolate % 2

        val prevSize = if (steps % 2 == 0) prevCov.even!!.layer.equalized!!.size else prevCov.odd!!.layer.equalized!!.size
        val lastSize = if (steps % 2 == 0) lastCov.even!!.layer.equalized!!.size else lastCov.odd!!.layer.equalized!!.size
        val a1 = 4 * lastLayer * prevSize + 4 * prevSize + 4 * lastLayer * lastSize + 8 * lastSize

        if (remDouble % 2 == 1) {
            count += 4.toBigInteger() * (lastLayer + layersToExtrapolate).toBigInteger() * prevSize.toBigInteger()
        }

        val d = 8 * (prevSize + lastSize)
        count += (BigInteger.TWO * a1.toBigInteger() + d.toBigInteger() * (doubleLayers.toBigInteger() - BigInteger.ONE)) * doubleLayers.toBigInteger() / BigInteger.TWO

        val startStepForFinalRound = steps - remAfterExtra

        val lastLayersAndOutskirts =
            (if (startStepForFinalRound % 2 == 0) lastCov.even!! else lastCov.odd!!)
        val prevLayersAndOutskirts =
            (if (startStepForFinalRound % 2 == 0) prevCov.even!! else prevCov.odd!!)

        val layersAndOutskirtsForLastRound = if (layersToExtrapolate % 2 == 0) {
            lastLayersAndOutskirts
        } else {
            prevLayersAndOutskirts
        }

        val replayed = footprintTracker.replay(layersAndOutskirtsForLastRound.shifted, remAfterExtra)

        var lastOutskirtsLayer = lastLayer + layersToExtrapolate + 1
        count += replayed.layer.sizeAtLayer(lastOutskirtsLayer)
        for (os in replayed.outskirts) {
            lastOutskirtsLayer++
            count += os.sizeAtLayer(lastOutskirtsLayer)
        }

        return count
    }


    private data class QueueItem(val coordinates: GlobalCoordinates, val layer: Int = 0)
    data class Parsed(val garden: Garden, val start: InTileCoordinates)

    companion object {
        fun from(lines: Sequence<String>): Parsed {
            var w: Int? = null
            var h = 0
            val rocks = mutableSetOf<InTileCoordinates>()
            var start: InTileCoordinates? = null
            for ((y, line) in lines.withIndex()) {
                require(w == null || line.length == w)
                w = line.length
                for ((x, char) in  line.withIndex()) {
                    when (char) {
                        '#' -> rocks.add(InTileCoordinates(x, y))
                        'S' -> {
                            require(start == null)
                            start = InTileCoordinates(x, y)
                        }
                    }
                }
                h++
            }

            require(h != 0)
            check(w != null)
            require(start != null)
            return Parsed(Garden(rocks, w, h), start)
        }
    }
}

fun countCellsInf(lines: Sequence<String>, limit: Int = 26501365, naive: Boolean = false): BigInteger {
    val (garden, start) = Garden.from(lines)
    return garden.coveredCoords(TileAndLocal(TileCoordinates(0, 0), start), limit, naive)
}