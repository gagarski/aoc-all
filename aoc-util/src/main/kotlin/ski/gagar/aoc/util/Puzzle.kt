package ski.gagar.aoc.util

import java.io.InputStream
import java.nio.file.Path

interface Puzzle {
    open val fileName: String
        get() = "${this::class.simpleName!!}.txt"
    val name: String

    fun part1(input: InputStream): Any? = null
    fun part2(input: InputStream): Any? = null

    private fun runPart1(basePath: Path, contest: Contest) =
        getInput(basePath, contest, this).use {
            part1(it)
        }
    private fun runPart2(basePath: Path, contest: Contest) =
        getInput(basePath, contest, this).use {
            part2(it)
        }

    fun run(basePath: Path, contest: Contest) {
        println("${this.javaClass.simpleName}: $name")
        runPart1(basePath, contest)?.let {
            println("\tPart 1:")
            println(it.indentOutput())
        }
        runPart2(basePath, contest)?.let {
            println("\tPart 2:")
            println(it.indentOutput())
        }
    }

    private fun Any.indentOutput(level: Int = 2) =
        toString().lineSequence().map { "\t".repeat(level) + it }.joinToString("\n")
}