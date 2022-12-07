package ski.gagar.aoc2015.day12.part2

import com.fasterxml.jackson.databind.ObjectMapper
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day11.part1.nextCompliantPassword

fun Any.traverseForInts(callable: (Int) -> Unit) {
    when (this) {
        is Int -> callable(this)
        is List<*> -> {
            for (item in this) {
                item?.traverseForInts(callable)
            }
        }
        is Map<*, *> -> {
            if (values.contains("red"))
                return
            for (item in this.values) {
                item?.traverseForInts(callable)
            }
        }
    }
}



fun Any.sumInts(): Int {
    var int = 0
    traverseForInts { int += it }
    return int
}

fun sumIntJson(json: String): Int {
    val mapper = ObjectMapper()
    val map = mapper.readValue(json, Map::class.java)
    return map.sumInts()
}


fun day12Part2() {
    println("day12/part2/json: ${
        sumIntJson(getResourceAsStream("/ski.gagar.aoc.aoc2015.day12/ints.json").bufferedReader().readText())
    }")
}
