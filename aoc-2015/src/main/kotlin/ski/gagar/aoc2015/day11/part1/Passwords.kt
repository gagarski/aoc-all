package ski.gagar.aoc2015.day11.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day10.part1.lookAnsSayLength
import java.lang.AssertionError
import java.math.BigInteger

val CHAR_RANGE = 'a'..'z'
const val PASSWORD_LENGTH = 8
const val GROWING_LENGTH = 3
const val REPEATED_LENGTH = 2
const val REPEATED_COUNT = 2

private val CharRange.size
    get() = last - first + 1

private fun String.toBigIntLetters(range: CharRange = CHAR_RANGE): BigInteger {
    require(range.size <= 26)
    val canonical = buildString {
        for (letter in this@toBigIntLetters) {
            require(letter in range)
            val digit = letter - 'a'
            append(
                when (digit) {
                    in 0 .. 9 -> '0' + digit
                    else -> 'a' + digit - 10
                }
            )
        }
    }
    return BigInteger(canonical, range.size)
}


private fun BigInteger.toLetters(range: CharRange = CHAR_RANGE, length: Int = PASSWORD_LENGTH): String {
    require(range.size <= 26)
    val canonical = toString(range.size)
    val nonCanonical = buildString {
        for (char in canonical) {
            append(
                when (char) {
                    in '0'..'9' -> range.first + (char - '0')
                    in 'a'..'z' -> range.first + (char - 'a' + 10)
                    else -> throw AssertionError("Should not happen")
                }
            )
        }
    }

    return when {
        nonCanonical.length > length -> nonCanonical.substring(nonCanonical.length - length, nonCanonical.length)
        nonCanonical.length < length -> nonCanonical.padStart(length, range.first)
        else -> nonCanonical
    }
}

fun String.isCompliantToPattern(range: CharRange = CHAR_RANGE, length: Int = PASSWORD_LENGTH) =
    this.length == length && all { it in CHAR_RANGE }

fun String.nextPassword(range: CharRange = CHAR_RANGE, length: Int = PASSWORD_LENGTH): String =
    (toBigIntLetters(range) + BigInteger.ONE).toLetters(range, length)


fun String.hasGrowingSequence(length: Int = GROWING_LENGTH): Boolean {
    require(length > 1)
    if (this.length < length) {
        return false
    }
    var lastChar: Char = this[0]
    var seqLength = 1
    for (char in asSequence().drop(1)) {
        if (char - lastChar == 1) {
            seqLength++
        } else {
            seqLength = 1
        }

        if (seqLength == length) {
            return true
        }
        lastChar = char
    }

    return false
}

val FORBIDDEN_CHARS = setOf('i', 'o', 'l')

fun String.hasNoForbiddenChars(forbiddenChars: Set<Char> = FORBIDDEN_CHARS) =
    all { it !in forbiddenChars }

fun String.hasRepeatedSeq(length: Int = REPEATED_LENGTH, count: Int = REPEATED_COUNT): Boolean {
    val seqs = mutableSetOf<String>()
    for (i in length .. this.length) {
        val seq = substring(i - length, i)

        if (seq.toSet().size == 1) {
            // Do not care about overlapping, different sequences cannot overlap
            seqs.add(seq)
        }

        if (seqs.size == count) {
            return true
        }
    }
    return false
}

fun String.isCompliant(
    range: CharRange = CHAR_RANGE,
    passwordLength: Int = PASSWORD_LENGTH,
    growingLength: Int = GROWING_LENGTH,
    forbiddenChars: Set<Char> = FORBIDDEN_CHARS,
    repeatedLength: Int = REPEATED_LENGTH,
    repeatedCount: Int = REPEATED_COUNT
) =
    isCompliantToPattern(range, passwordLength) && hasGrowingSequence(growingLength) &&
            hasNoForbiddenChars(forbiddenChars) && hasRepeatedSeq(repeatedLength, repeatedCount)

fun String.nextCompliantPassword(
    range: CharRange = CHAR_RANGE,
    passwordLength: Int = PASSWORD_LENGTH,
    growingLength: Int = GROWING_LENGTH,
    forbiddenChars: Set<Char> = FORBIDDEN_CHARS,
    repeatedLength: Int = REPEATED_LENGTH,
    repeatedCount: Int = REPEATED_COUNT
): String {
    var password = this
    do {
        password = password.nextPassword(range, length)
    } while (!password.isCompliant(range, passwordLength, growingLength, forbiddenChars, repeatedLength, repeatedCount))
    return password
}