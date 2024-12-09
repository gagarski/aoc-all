package ski.gagar.aoc2015.day16.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day16.part1.AuntCharacteristic
import ski.gagar.aoc2015.day16.part1.AuntDescription
import ski.gagar.aoc2015.day16.part1.AuntParser
import ski.gagar.aoc2015.day16.part1.UnknownAunt

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


fun findFirstMatching(descs: Sequence<String>,
                      unknownAunt: UnknownAunt = UnknownAunt(
                          mapOf(
                              AuntCharacteristic.CHILDREN to 3,
                              AuntCharacteristic.CATS to 7,
                              AuntCharacteristic.SAMOYEDS to 2,
                              AuntCharacteristic.POMERANIANS to 3,
                              AuntCharacteristic.AKITAS to 0,
                              AuntCharacteristic.VIZSLAS to 0,
                              AuntCharacteristic.GOLDFISH to 5,
                              AuntCharacteristic.TREES to 3,
                              AuntCharacteristic.CARS to 2,
                              AuntCharacteristic.PERFUMES to 1
                          )
                      )) =
    findFirstMatching(unknownAunt, descs.map { AuntParser.parseDescription(it) })?.index
