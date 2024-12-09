package ski.gagar.aoc.util

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

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

fun getInput(base: Path, contest: Contest, puzzle: Puzzle): InputStream =
    FileInputStream(base.resolve(contest.name).resolve(puzzle.fileName).toFile())