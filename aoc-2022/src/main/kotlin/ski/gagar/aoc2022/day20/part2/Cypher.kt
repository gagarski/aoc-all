package ski.gagar.aoc2022.day20.part2

import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.absoluteValue
import kotlin.math.sign

class MixNode(val value: Long) {
    lateinit var prev: MixNode
    lateinit var oldPrev: MixNode
    lateinit var next: MixNode
    lateinit var oldNext: MixNode

    fun forgetOld() {
        oldPrev = prev
        oldNext = next
    }

    override fun toString(): String = "MixNode($value)"
}

class Mixer(init: Iterable<Long>) {
    val size: Int
    var start: MixNode? = null

    init {
        var current: MixNode? = null
        var size = 0

        for (i in init) {
            size++

            val new = MixNode(value = i)

            if (null != current) {
                new.prev = current
                new.oldPrev = current
                current.next = new
                current.oldNext = new
            }

            current = new

            if (start == null) {
                start = current
            }
        }

        val start = this.start

        if (current != null && start != null) {
            start.prev = current
            start.oldPrev = current
            current.next = start
            current.oldNext = start
        }

        this.size = size
    }

    private fun forgetOld() {
        val start = start ?: return
        var current = start

        do {
            current.forgetOld()
            current = current.next
        } while (current != start)
    }

    fun asSequence() = sequence {
        val start = start ?: return@sequence
        var current = start

        do {
            yield(current.value)
            current = current.next
        } while (current != start)
    }

    private fun MixNode.getNodeInDirection(offset: Long, direction: MixNode.() -> MixNode): MixNode {
        var current = this

        for (i in 0 until offset) {
            current = current.direction()
        }
        return current
    }

    private fun MixNode.removeFromCurrentPlace() {
        val start = start

        if (start == this) {
            this@Mixer.start = if (start.next == this) null else start.next
        }

        prev.next = next
        next.prev = prev
    }

    private fun MixNode.insertBefore(other: MixNode) {
        if (other == this) return
        val predecessor = other.prev
        other.prev.next = this
        other.prev = this
        next = other
        prev = predecessor
    }

    private fun MixNode.insertAfter(other: MixNode) {
        if (other == this) return
        val successor = other.next
        other.next.prev = this
        other.next = this
        prev = other
        next = successor
    }

    private fun MixNode.getNodeBefore(offset: Long): MixNode =
        getNodeInDirection(offset) { prev }

    private fun MixNode.getNodeAfter(offset: Long): MixNode =
        getNodeInDirection(offset) { next }

    private fun mixSingle(start: MixNode) {
        var current = start

        do {
            val value = current.value
            val truncated = value % (size - 1)

            val inversed = when {
                truncated.absoluteValue >= size / 2 -> (size - 1 - truncated.absoluteValue) * (-truncated.sign)
                else -> truncated
            }

            val movementDirection = inversed

            if (movementDirection == 0L) {
                current = current.oldNext
                continue
            }

            current.removeFromCurrentPlace()

            when {
                movementDirection < 0 -> current.insertBefore(current.getNodeBefore(-movementDirection))
                else -> current.insertAfter(current.getNodeAfter(movementDirection))
            }

            current = current.oldNext
        } while (current != start)
    }

    fun mix(n: Int = 1) {
        val start = start ?: return
        for (i in 0 until n) {
            mixSingle(start)
        }
        forgetOld()
    }
}

fun getResult(lines: Sequence<String>,
              indices: Set<Int> = setOf(1000, 2000, 3000),
              beacon: Long = 0L,
              nMix: Int = 10,
              mul: Long = 811589153
): Long {
    val mixer = Mixer(lines.map { it.toLong() * mul }.asIterable())

    mixer.mix(nMix)

    val res = mixer.asSequence().toList()

    val beaconIndex = res.indexOf(beacon)

    check(beaconIndex >= -1)

    return indices.sumOf { res[(it + beaconIndex) % res.size] }
}
