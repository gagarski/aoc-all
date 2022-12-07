package ski.gagar.aoc2015.day16.part3

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day16.part1.AuntCharacteristic
import ski.gagar.aoc2015.day16.part1.AuntDescription
import ski.gagar.aoc2015.day16.part1.AuntParser
import ski.gagar.aoc2015.day16.part1.UnknownAunt
import java.lang.IllegalStateException

//
//enum class AuntCharacteristic {
//    CHILDREN, CATS, SAMOYEDS, POMERANIANS, AKITAS, VIZSLAS, GOLDFISH, TREES, CARS, PERFUMES
//}
//
//data class AuntDescription(val index: Int, val chars: Map<AuntCharacteristic, Int>)
//
//data class UnknownAunt(val chars: Map<AuntCharacteristic, Int>) {
//    fun matches(desc: AuntDescription) =
//        desc.chars.all { (k, v) -> chars[k] == v }
//}

private val CHARS_GT = setOf(AuntCharacteristic.CATS, AuntCharacteristic.TREES)
private val CHARS_LT = setOf(AuntCharacteristic.POMERANIANS, AuntCharacteristic.GOLDFISH)

fun UnknownAunt.matches2(desc: AuntDescription) =
    desc.chars.all { (k, v) ->
        val myV = chars[k] ?: return@all false
        when {
            k in CHARS_GT -> myV < v
            k in CHARS_LT -> myV > v
            else -> myV == v
        }
    }

fun findFirstMatching(aunt: UnknownAunt, descs: Sequence<AuntDescription>) =
    descs.find { aunt.matches2(it) }


fun findFirstMatching(auntStrings: Sequence<String>, descs: Sequence<String>) =
    findFirstMatching(AuntParser.parseUnknownAunt(auntStrings), descs.map { AuntParser.parseDescription(it) })?.index

fun day16Part2() {
    println("day16/part2/aunts: ${
        findFirstMatching(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day16/unknown.txt").bufferedReader().lineSequence(),
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day16/descriptions.txt").bufferedReader().lineSequence()
        )
    }")
}
