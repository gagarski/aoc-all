package ski.gagar.aoc2024.day19.part1

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

class PatternTree(sequence: Sequence<String>) {
    val root = Node("<root>")

    init {
        for (pattern in sequence) {
            require(pattern.isNotEmpty())
            root.addPattern(pattern)
        }
    }

    fun hasPattern(pattern: String) = root.hasPattern(pattern)

    fun getPatternsForTowel(towel: String, startPos: Int = 0) = root.getPatternsForTowel(towel, startPos)

    private data class State(val patterns: PersistentList<String> = persistentListOf(), val startPos: Int = 0)

    fun getFirstPossible(towel: String): List<String>? {
        val stack = ArrayDeque<State>()

        stack.addFirst(State())

        while (stack.isNotEmpty()) {
            val state = stack.removeFirst()

            if (state.startPos == towel.length)
                return state.patterns

            val next = getPatternsForTowel(towel, state.startPos)

            for (n in next) { // Longest first
                stack.addFirst(State(state.patterns.add(n), state.startPos + n.length))
            }
        }
        return null
    }

    class Node(val name: String) {
        var isFinal: Boolean = false
            private set
        private val children_ = mutableMapOf<Char, Node>()

        val children: Map<Char, Node>
            get() = children_

        fun addPattern(pattern: String) {
            var node = this
            var depth = 0
            for (element in pattern) {
                node = node.children_.getOrPut(element) { Node("$element, depth: ${depth++}") }
            }
            node.isFinal = true
        }

        fun hasPattern(pattern: String): Boolean {
            var node = this

            for (element in pattern) {
                node = node.children[element] ?: return false
            }
            return node.isFinal
        }

        fun getPatternsForTowel(towel: String, startPos: Int = 0): List<String> {
            var node = this
            val patterns: MutableList<String> = mutableListOf()

            var interrupted = false

            for (i in startPos until towel.length) {
                if (node.isFinal)
                    patterns.add(towel.substring(startPos, i))

                val nxt = node.children[towel[i]]

                if (null == nxt) {
                    interrupted = true
                    break
                }

                node = nxt
            }

            if (node.isFinal && !interrupted)
                patterns.add(towel.substring(startPos))


            return patterns
        }

        override fun toString(): String = name
    }
}

fun countPossibleTowels(lines: Sequence<String>): Int {
    val itr = lines.iterator()

    require(itr.hasNext())

    val patterns = itr.next().split(", ")
    val tree = PatternTree(patterns.asSequence())

    require(itr.hasNext())
    itr.next() // ignoring empty line

    return itr.asSequence().count { towel ->
        tree.getFirstPossible(towel) != null
    }
}