package ski.gagar.aoc2024.day25.part1

sealed interface Pattern {
    val height: Int
    val width: Int
}

data class Key(val pattern: List<Int>, override val height: Int, override val width: Int) : Pattern {
    fun matches(lock: Lock): Boolean {
        if (lock.height != height) return false
        if (lock.width != width) return false

        for (i in 0 ..< width) {
            if (lock.pattern[i] + pattern[i] > height) return false
        }
        return true
    }
}
data class Lock(val pattern: List<Int>, override val height: Int, override val width: Int) : Pattern

fun parseKeyOrLock(lines: Iterator<String>): Pattern? {
    if (!lines.hasNext()) return null
    val first = lines.next()
    val w = first.length
    val isLock =
        if (first.all { it.equals('.') }) false
        else if (first.all { it.equals('#') }) true
        else error("Neither key nor lock")

    val counted = if (isLock) '#' else '.'
    val bottom = if (isLock) '.' else '#'

    val counters = MutableList(w) {0}

    var last: String? = null
    var h = 0
    for (line in lines) {
        if (line.isEmpty()) break
        require(line.length == w)
        for ((i, char) in line.withIndex()) {
            if (char == counted) {
                counters[i]++
            }
        }
        last = line
        h++
    }
    h--
    require(last != null)
    require(last.all { it == bottom })

    if (isLock) {
        return Lock(counters, h, w)
    } else {
        return Key(counters.map { h - it }, h, w)
    }
}

fun countMatchingPairs(lines: Sequence<String>): Int {
    val keys = mutableListOf<Key>()
    val locks = mutableListOf<Lock>()
    val itr = lines.iterator()
    while (true) {
        val pattern = parseKeyOrLock(itr)
        when (pattern) {
            is Key -> keys.add(pattern)
            is Lock -> locks.add(pattern)
            null -> break
        }
    }
    var cnt = 0

    for (key in keys) {
        for (lock in locks) {
            if (key.matches(lock))
                cnt++
        }
    }
    return cnt
}