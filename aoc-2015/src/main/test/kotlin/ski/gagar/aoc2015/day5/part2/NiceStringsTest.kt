package ski.gagar.aoc2015.day5.part2

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class NiceStringsTest {

    @Test
    fun testIsPalindrome() {
        assertTrue(isPalindrome(""))
        assertTrue(isPalindrome("a"))
        assertTrue(isPalindrome("abcba"))
        assertTrue(isPalindrome("abba"))
        assertFalse(isPalindrome("ab"))
        assertFalse(isPalindrome("abcd"))
        assertFalse(isPalindrome("abcde"))
        assertTrue(isPalindrome("abcddcba"))
    }

    @Test
    fun testHasRepeatedSequence() {
        assertTrue(hasRepeatedSeq("aabdefegaafd", 2))
        assertTrue(hasRepeatedSeq("aabdefegaa", 2))
        assertTrue(hasRepeatedSeq("caabdefegaa", 2))
        assertTrue(hasRepeatedSeq("caaabdefegaa", 2))
        assertTrue(hasRepeatedSeq("aaaa", 2))
        assertFalse(hasRepeatedSeq("caaabdefeg", 2))

        assertTrue(hasRepeatedSeq("abcdefggabcdef", 6))
        assertFalse(hasRepeatedSeq("aaaaaaaaaa", 6))
    }

    @Test
    fun testHasFixedLengthPalindrome() {
        assertFalse(hasFixedLengthPalindrome("", 3))
        assertFalse(hasFixedLengthPalindrome("xx", 3))
        assertTrue(hasFixedLengthPalindrome("aba", 3))
        assertTrue(hasFixedLengthPalindrome("aaa", 3))
        assertTrue(hasFixedLengthPalindrome("aa", 2))
        assertTrue(hasFixedLengthPalindrome("asdasdabasda", 3))
        assertFalse(hasFixedLengthPalindrome("asdasdabcsda", 3))
        assertTrue(hasFixedLengthPalindrome("aabcddcbasda", 8))
    }

    @Test
    fun testIsNice() {
        assertFalse(isNice("", 2, 3))
        assertTrue(isNice("aafsdakghjfsdaahkdfsdfxyxasdasd", 2, 3))
    }
}
