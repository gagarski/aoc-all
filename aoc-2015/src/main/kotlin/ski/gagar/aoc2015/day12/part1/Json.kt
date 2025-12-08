package ski.gagar.aoc2015.day12.part1

import tools.jackson.databind.ObjectMapper

fun Any.traverseForInts(callable: (Int) -> Unit) {
    when (this) {
        is Int -> callable(this)
        is List<*> -> {
            for (item in this) {
                item?.traverseForInts(callable)
            }
        }
        is Map<*, *> -> {
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
