package ski.gagar.aoc2024.day9.part1

import java.util.NavigableSet
import java.util.TreeSet

data class MoveResult(
    val newFile: FileChunk,
    val newFree: FreeChunk?,
    val oldFree: FreeChunk,
    val oldRem: FileChunk?
)
interface Chunk : Comparable<Chunk> {
    val start: Long
    val length: Long
    val endInclusive: Long
        get() = start + length - 1
    override fun compareTo(other: Chunk): Int = start.compareTo(other.start)
}

data class FreeChunk(override val start: Long, override val length: Long) : Chunk
data class FileChunk(val id: Int, override val start: Long, override val length: Long) : Chunk {
    fun moveTo(free: FreeChunk): MoveResult {
        require(free.length > 0)
        val diff = free.length - length
        val actuallyMoved = minOf(free.length, length)

        val newFile = FileChunk(id, free.start, actuallyMoved)
        val oldFree = FreeChunk(start + diff, actuallyMoved)
        val newFree = if (diff > 0) FreeChunk(free.start + actuallyMoved, diff) else null
        val oldRem = if (diff < 0) FileChunk(id, start, -diff) else null

        return MoveResult(newFile, newFree, oldFree, oldRem)
    }

    fun isContinuousWith(other: FileChunk): Boolean {
        if (id != other.id) return false

        return endInclusive + 1 == other.start
    }

    operator fun plus(other: FileChunk): FileChunk {
        require(isContinuousWith(other))
        return FileChunk(id, start, length + other.length)
    }
}

private fun <T> NavigableSet<T>.lastOrNull() = try {
    last
} catch (e: NoSuchElementException) {
    null
}

class FileSystem(chars: Sequence<Char>) {
    val files: NavigableSet<FileChunk>
    val free: NavigableSet<FreeChunk>

    init {
        var state = ParseState.FILE
        var index: Long = 0
        var fileId: Int = 0
        val files = TreeSet<FileChunk>()
        val free = TreeSet<FreeChunk>()

        for (char in chars) {
            val size = char - '0'
            when (state) {
                ParseState.FILE -> {
                    files.add(FileChunk(fileId, index, size.toLong()))
                    fileId++
                    state = ParseState.FREE
                }
                ParseState.FREE -> {
                    if (size != 0)
                        free.add(FreeChunk(index, size.toLong()))
                    state = ParseState.FILE
                }
            }
            index += size
        }
        this.files = files
        this.free = free
        trimFreeWhilePossible()
    }

    fun fragment() {
        if (files.isEmpty())
            return
        while (free.isNotEmpty()) {
            val firstFree = free.pollFirst()!!
            val lastFile = files.pollLast()!!
            val moveRes = lastFile.moveTo(firstFree)
            files.add(moveRes.newFile)
            moveRes.newFree?.let {
                free.add(it)
            }
            moveRes.oldRem?.let {
                files.add(it)
            }
            // free.add(moveRes.oldFree)
            trimFreeWhilePossible()
        }

        mergeFiles()
    }

    private fun FileChunk.crc(): Long = id * length * (2 * start + length - 1) / 2

    fun crc() = files.asSequence().map {
        it.crc()
    }.sum()

    private fun trimFree(): Boolean {
        val lastFree = free.lastOrNull() ?: return false
        val lastFile = files.lastOrNull()

        if (lastFile == null || lastFree.start > lastFile.endInclusive) {
            free.remove(lastFree)
            return true
        }

        return false
    }

    private fun mergeFiles() {
        var prev: FileChunk? = null
        val toRemove = mutableSetOf<FileChunk>()
        val toAdd = mutableSetOf<FileChunk>()
        for (file in files) {
            if (prev?.isContinuousWith(file) == true) {
                toRemove.add(file)
                toRemove.add(prev)
                toAdd.add(prev + file)
            }
            prev = file
        }

        val itr = files.iterator()
        while (itr.hasNext()) {
            val f = itr.next()
            if (f in toRemove) itr.remove()
        }

        for (f in toAdd) {
            files.add(f)
        }
    }

    private fun trimFreeWhilePossible() {
        @Suppress("ControlFlowWithEmptyBody")
        while (trimFree()) {}
    }

    private enum class ParseState {
        FILE, FREE
    }
}

fun fragmentCrc(fsChars: Sequence<Char>): Long {
    val fs = FileSystem(fsChars)
    fs.fragment()
    return fs.crc()
}
