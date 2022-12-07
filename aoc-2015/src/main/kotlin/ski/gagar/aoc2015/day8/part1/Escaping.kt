package ski.gagar.aoc2015.day8.part1

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import ski.gagar.aoc.util.getResourceAsStream

object EscapedStringParser {
    private val HEX_DIGIT = Scanners.isChar {
        it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F'
    }.source().map {
        it.toInt(16)
    }
    private val SMALL_HEX = Parsers.sequence(
        HEX_DIGIT, HEX_DIGIT
    ) { first, second ->
        first * 16 + second
    }
    private val CHAR = Parsers.or(
        Parsers.sequence(Scanners.string("\\x"), SMALL_HEX) { _, hex ->
            hex.toChar()
        },
        Scanners.string("""\\""").source().map {
            '\\'
        },
        Scanners.string("""\"""").source().map {
            '"'
        },
        Scanners.isChar {
            it != '\"'
        }.source().map { it.first() }
    )

    private val STRING = Parsers.sequence(
        Scanners.string("\""),
        CHAR.many().map {
            it.joinToString("")
        },
        Scanners.string("\"")
    ) { _, str, _ ->
        str
    }

    fun parse(string: String) = STRING.parse(string)
}

fun countDiff(strings: Sequence<String>) =
    strings.map { it to EscapedStringParser.parse(it) }.sumOf { it.first.length - it.second.length }

fun day8Part1() {
    println("day8/part1/escaping: ${
        countDiff(getResourceAsStream("/ski.gagar.aoc.aoc2015.day8/escaping.txt").bufferedReader().lineSequence())
    }")
}
