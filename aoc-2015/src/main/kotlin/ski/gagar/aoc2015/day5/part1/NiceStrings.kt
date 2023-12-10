package ski.gagar.aoc2015.day5.part1

import ski.gagar.aoc2015.day4.part2.bruteforceCoins
import ski.gagar.aoc.util.getResourceAsStream

private val VOWELS = setOf('a', 'e', 'i', 'o', 'u')
private val BAD = setOf("ab", "cd", "pq", "xy")

fun enoughVowels(str: String, n: Int = 3, vowels: Set<Char> = VOWELS): Boolean {
    var cnt = 0
    for (char in str) {
        if (char in vowels) cnt++

        if (cnt == n) return true
    }

    return false
}

fun hasRepeatedLetter(str: String, n: Int = 2): Boolean {
    var prevChar: Char? = null
    var repeatCount = 1
    for (char in str) {
        if (char == prevChar) {
            repeatCount++
        } else {
            repeatCount = 1
        }

        if (repeatCount == n) {
            return true
        }
        prevChar = char
    }

    return false
}

fun hasBadDigrams(str: String, bad: Set<String> = BAD): Boolean {
    require(bad.all { it.length == 2 })

    if (str.length < 2) return false

    for (i in 1 until str.length) {
        val sub = str.substring(i - 1, i + 1)

        if (sub in bad) return true
    }

    return false
}

fun isNice(
    str: String,
    nVowels: Int = 3,
    nRepeats: Int = 2,
    vowels: Set<Char> = VOWELS,
    bad: Set<String> = BAD
) =
    enoughVowels(str, nVowels, vowels) && hasRepeatedLetter(str, nRepeats) && !hasBadDigrams(str, bad)


fun countNice(
    strings: Sequence<String>,
    nVowels: Int = 3,
    nRepeats: Int = 2,
    vowels: Set<Char> = VOWELS,
    bad: Set<String> = BAD
) = strings.count { isNice(it, nVowels, nRepeats, vowels, bad) }
