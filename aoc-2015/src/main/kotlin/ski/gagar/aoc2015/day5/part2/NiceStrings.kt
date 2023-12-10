package ski.gagar.aoc2015.day5.part2

import ski.gagar.aoc.util.getResourceAsStream

fun hasRepeatedSeq(str: String, length: Int = 2): Boolean {
    if (str.length < length * 2)
        return false
    val seenDigrams = mutableSetOf<String>()
    val overlapping = ArrayDeque<String>()

    fun remember(seq: String) {
        overlapping.addLast(seq)
        if (overlapping.size > length - 1) {
            val popped = overlapping.removeFirst()
            seenDigrams.add(popped)
        }
    }

    for (i in length .. str.length) {
        val seq = str.substring(i - length, i)

        if (seenDigrams.contains(seq))
            return true
        remember(seq)
    }
    return false
}

fun isPalindrome(str: String): Boolean {
    for (i in 0 until str.length / 2) {
        if (str[i] != str[str.length - 1 - i]) return false
    }
    return true
}

fun hasFixedLengthPalindrome(str: String, length: Int = 3): Boolean {
    for (i in length .. str.length) {
        val sub = str.substring(i - length, i)

        if (isPalindrome(sub))
            return true
    }
    return false
}

fun isNice(
    str: String,
    repeatedLength: Int = 2,
    palindromeLength: Int = 3
) =
    hasRepeatedSeq(str, repeatedLength) && hasFixedLengthPalindrome(str, palindromeLength)


fun countNice(
    strings: Sequence<String>,
    repeatedLength: Int = 2,
    palindromeLength: Int = 3
) = strings.count { isNice(it, repeatedLength, palindromeLength) }
