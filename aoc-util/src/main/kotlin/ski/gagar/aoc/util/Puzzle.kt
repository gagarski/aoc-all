package ski.gagar.aoc.util

import java.io.InputStream

interface Puzzle {
    val name: String
    val inputPath: String

    fun part1(input: InputStream): Any? = null
    fun part2(input: InputStream): Any? = null

    private fun runPart1() =
        getResourceAsStream(inputPath).use {
            part1(it)
        }
    private fun runPart2() =
        getResourceAsStream(inputPath).use {
            part2(it)
        }

    fun run() {
        println("${this.javaClass.simpleName}: $name")
        runPart1()?.let {
            println("\tPart 1:")
            println(it.indentOutput())
        }
        runPart2()?.let {
            println("\tPart 2:")
            println(it.indentOutput())
        }
    }

    private fun Any.indentOutput(level: Int = 2) =
        toString().lineSequence().map { "\t".repeat(level) + it }.joinToString("\n")
}