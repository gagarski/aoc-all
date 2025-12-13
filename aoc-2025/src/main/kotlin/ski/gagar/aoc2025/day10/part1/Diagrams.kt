package ski.gagar.aoc2025.day10.part1

import java.util.BitSet

class State private constructor(val size: Int, private val lights: BitSet) {
    constructor(size: Int) : this(size, BitSet(size))

    fun isOn(index: Int): Boolean {
        require(index in 0 until size)
        return lights[index]
    }

    fun toggle(index: Int): State {
        require(index in 0 until size)
        val newBitset = lights.clone() as BitSet
        newBitset.flip(index)
        return State(size, newBitset)
    }

    fun press(button: Button): State {
        require(button.size <= size)
        val newBitset = lights.clone() as BitSet
        newBitset.xor(button.toggles)
        return State(size, newBitset)
    }

    companion object {
        private val RE = """\[([.#]+)]""".toRegex()
        fun parse(string: String): State {
            val match = RE.matchEntire(string)
            check(match != null)
            val bits = match.groupValues[1]

            val bitSet = BitSet(bits.length)

            for ((index, bit) in bits.withIndex()) {
                when (bit) {
                    '.' -> {}
                    '#' -> bitSet.set(index)
                }
            }
            return State(bits.length, bitSet)
        }
    }

    override fun toString(): String = buildString {
        append('[')
        for (i in 0 ..<size) {
            when (lights[i]) {
                true -> append('#')
                false -> append('.')
            }
        }
        append(']')
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (size != other.size) return false
        if (lights != other.lights) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + lights.hashCode()
        return result
    }
}

class Button private constructor(val size: Int, val toggles: BitSet) {
    fun toggles(i: Int) = toggles[i]

    companion object {
        fun parse(string: String, size: Int): Button {
            require(string.first() == '(')
            require(string.last() == ')')
            val bitSet = BitSet()
            val nums = string.substring(1, string.length - 1).split(",").map { it.toInt() }.toList()
            for (int in string.substring(1, string.length - 1).split(",").map { it.toInt() }) {
                require(int in 0..<size)
                bitSet.set(int)
            }
            return Button(size, bitSet)
        }
    }
}

class Joltages(val levels: List<Int>) {
    operator fun get(i: Int) = levels[i]
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Joltages

        return levels == other.levels
    }

    override fun hashCode(): Int {
        return levels.hashCode()
    }

    override fun toString(): String {
        return "Joltages(levels=$levels)"
    }

    companion object {
        fun parse(string: String, size: Int): Joltages {
            require(string.first() == '{')
            require(string.last() == '}')
            val nums = string.substring(1, string.length - 1).split(",").map { it.toInt() }.toList()
            require(nums.size == size)
            return Joltages(nums)
        }
    }
}

private data class QueueItem(val state: State, val pathLength: Int = 0)

class Diagram private constructor(
    val targetState: State,
    val buttons: List<Button>,
    val requirements: Joltages
) {
    fun shortestPathToTargetState(): Int {
        val queue = ArrayDeque<QueueItem>()
        queue.add(QueueItem(State(targetState.size)))
        val visitedStates = mutableSetOf<State>()
        while (!queue.isEmpty()) {
            val (state, pathLength) = queue.removeFirst()
            if (state == targetState) return pathLength

            if (visitedStates.contains(state)) continue
            visitedStates.add(state)

            for (button in buttons) {
                val newState = state.press(button)
                if (newState in visitedStates)
                    continue
                queue.add(QueueItem(newState, pathLength + 1))
            }
        }
        return -1
    }

    companion object {
        fun parse(line: String): Diagram {
            val chunks = line.split(" ")
            require(chunks.size >= 2)
            val state = State.parse(chunks[0])
            val joltageRequirements = Joltages.parse(chunks.last(), state.size)
            val buttons = chunks.dropLast(1).asSequence().drop(1).map { Button.parse(it, state.size) }.toList()
            return Diagram(state, buttons, joltageRequirements)
        }

    }
}

fun sumShortestPaths(lines: Sequence<String>): Int =
    lines.map { Diagram.parse(it).shortestPathToTargetState() }.sum()