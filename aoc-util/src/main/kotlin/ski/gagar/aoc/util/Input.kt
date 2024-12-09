package ski.gagar.aoc.util

import java.io.BufferedReader

private class Input

fun getResourceAsStream(path: String) = Input::class.java.getResourceAsStream(path)!!

fun BufferedReader.readTextAndClose(trimTrailingNewLine: Boolean = true): String =
    use { br ->
        br.readText().run {
            if (trimTrailingNewLine)
                trimEnd('\n')
            else
                this
        }
    }