package ski.gagar.aoc2015.day4.part2

import ski.gagar.aoc.util.getResourceAsStream
import java.security.MessageDigest
import kotlin.experimental.and

const val MD5_SIZE = 16
const val MD5_HEX_DIGITS = MD5_SIZE * 2

fun isZeroHexDigitAt(bytes: ByteArray, index: Int): Boolean {
    require(index < bytes.size * 2)
    val byte = bytes[index / 2]
    return byte and (if (index % 2 == 0) 0x0f.toByte() else 0xf0.toByte()) == 0.toByte()
}

fun hashCompliant(bytes: ByteArray, leadingZeros: Int = 6): Boolean {
    require(leadingZeros <= MD5_HEX_DIGITS)
    if (bytes.size != MD5_SIZE)
        return false

    return (0 until leadingZeros).all { isZeroHexDigitAt(bytes, it) }
}



fun md5WithNumber(string: String, num: Int): ByteArray {
    val toHash = "$string$num"
    val digest = MessageDigest.getInstance("MD5")
    digest.update(toHash.toByteArray())
    return digest.digest()
}

fun isCoin(key: String, number: Int, leadingZeros: Int = 6) = hashCompliant(md5WithNumber(key, number), leadingZeros)

fun bruteforceCoins(key: String, leadingZeros: Int = 6): Int {
    for (i in 0 .. Int.MAX_VALUE) {
        if (isCoin(key, i, leadingZeros)) {
            return i
        }
    }

    return -1
}
