package ski.gagar.aoc2015.day4.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.security.MessageDigest
import kotlin.experimental.and

const val MD5_SIZE = 16

fun hashCompliant(bytes: ByteArray): Boolean {
    if (bytes.size != MD5_SIZE)
        return false
    return bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && (bytes[2] and 0xf0.toByte()) == 0.toByte()
}

fun md5WithNumber(string: String, num: Int): ByteArray {
    val toHash = "$string$num"
    val digest = MessageDigest.getInstance("MD5")
    digest.update(toHash.toByteArray())
    return digest.digest()
}

fun isCoin(key: String, number: Int) = hashCompliant(md5WithNumber(key, number))

fun bruteforceCoins(key: String): Int {
    for (i in 0 .. Int.MAX_VALUE) {
        if (isCoin(key, i)) {
            return i
        }
    }

    return -1
}


fun day4Part1() {
    println(
        "day4/part1/coins: ${
            bruteforceCoins(getResourceAsStream("/ski.gagar.aoc.aoc2015.day4/coins.txt").bufferedReader().lineSequence().first())
        }"
    )
}
