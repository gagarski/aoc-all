package ski.gagar.aoc2022.day6.part1

import ski.gagar.aoc.util.getResourceAsStream


fun startOffset(string: String, nDifferent: Int = 4): Int {
    require(nDifferent > 0)
    val deque = ArrayDeque<Char>()
    val map = mutableMapOf<Char, Int>()

    fun removeFromMap(char: Char) {
        val newCount = (map[char] ?: 0) - 1

        assert(newCount >= 0)

        if (newCount == 0)
            map.remove(char)
        else
            map[char] = newCount
    }

    fun addToMap(char: Char) {
        map[char] = (map[char] ?: 0) + 1
    }

    fun remember(char: Char) {
        deque.addLast(char)
        addToMap(char)

        if (deque.size > nDifferent) {
            val removed = deque.removeFirst()
            removeFromMap(removed)
        }
    }

    for ((ix, char) in string.withIndex()) {
        if (map.size == nDifferent)
            return ix
        remember(char)
    }

    return -1
}
