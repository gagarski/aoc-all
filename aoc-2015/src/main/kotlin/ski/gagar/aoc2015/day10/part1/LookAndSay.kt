package ski.gagar.aoc2015.day10.part1

fun lookAndSay(string: String): String {
    var last: Char? = null
    var repeatCtr = 1
    return buildString {
        for (char in string) {
            if (char != last) {
                if (last != null) {
                    append(repeatCtr)
                    append(last)
                }
                last = char
                repeatCtr = 1
            } else {
                repeatCtr++
            }
        }

        if (last != null) {
            append(repeatCtr)
            append(last)
        }
    }
}

fun lookAndSayLength(string: String, iterations: Int = 40): Int {
    var current = string

    for (i in 0 until iterations) {
        current = lookAndSay(current)
    }

    return current.length
}
