package ski.gagar.aoc2024.day9

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day9.part1.fragmentCrc
import ski.gagar.aoc2024.day9.part2.defragmentCrc
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "Disk Fragmenter"
    override val inputPath: String = "/ski.gagar.aoc.aoc2024.day9/fs.txt"

    override fun part1(input: InputStream) =
        fragmentCrc(input.buffered().iterator().asSequence().map { it.toInt().toChar() })
    override fun part2(input: InputStream) = defragmentCrc(input.buffered().iterator().asSequence().map { it.toInt().toChar() })
}