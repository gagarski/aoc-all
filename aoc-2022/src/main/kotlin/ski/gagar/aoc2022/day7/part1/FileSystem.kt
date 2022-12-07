package ski.gagar.aoc2022.day7.part1

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import ski.gagar.aoc.util.getResourceAsStream
import java.io.FileNotFoundException

interface FsItem {
    val name: String
    fun size(): Int
}

data class File(override val name: String, val size: Int) : FsItem {
    override fun size(): Int = size
}

data class Directory(override val name: String) : FsItem {
    private var children_: Map<String, FsItem>? = null
        private set

    val size_: Int by lazy {
        check(null != children_)
        children_!!.values.sumOf { it.size() }
    }

    override fun size(): Int = size_


    fun addListing(files: Iterable<FsItem>) {
        children_ = files.associateBy { it.name }
    }

    fun child(name: String): FsItem {
        check(null != children_)
        return children_!![name] ?: throw FileNotFoundException("File $name not found")
    }

    val children: Map<String, FsItem> by lazy {
        require(null != children_)
        children_!!
    }
}

data class Path(val chunks: List<String> = listOf())

data class LsItem(val path: Path, val fsItem: FsItem)

class FileSystem {
    val rootDir = Directory("")

    fun addListing(path: Path, items: Iterable<FsItem>) {
        val dir = this[path]
        require(dir is Directory)
        dir.addListing(items)
    }

    operator fun get(path: Path): FsItem {
        var current: FsItem = rootDir

        for (chunk in path.chunks) {
            check(current is Directory)
            current = current.child(chunk)
        }

        return current
    }

    private data class StackItem(val path: PersistentList<String>, val file: FsItem)



    fun lsR(path: Path = Path(listOf())): List<LsItem> {
        val res = mutableListOf<LsItem>()
        val stack = ArrayDeque<StackItem>()

        stack.addLast(StackItem(path.chunks.toPersistentList(), this[path]))

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            res.add(LsItem(Path(current.path), current.file))

            if (current.file is Directory) {
                for ((k, v) in current.file.children) {
                    stack.addLast(StackItem(current.path.add(k), v))
                }
            }
        }

        return res
    }
}

class Console {

    private var cwdPath = Path()
    val fs = FileSystem()
    private var cwd: Directory = fs[cwdPath] as Directory

    abstract class Cmd {
        protected val outputBuf = mutableListOf<String>()

        fun consumeOutput(line: String) {
            outputBuf.add(line)
        }

        abstract fun run()
    }

    inner class Ls : Cmd() {
        private fun parseItem(line: String): FsItem {
            val match = LS_ITEM_RE.matchEntire(line)
            require(match != null)

            val first = match.groups[1]!!.value
            val name = match.groups[2]!!.value

            return when (first) {
                DIR -> Directory(name)
                else -> File(name, first.toInt())
            }
        }

        override fun run() {
            cwd.addListing(outputBuf.map { parseItem(it) })
        }

    }

    inner class Cd(val arg: String) : Cmd() {
        override fun run() {
            val path = when (arg) {
                "/" -> Path()
                else -> {
                    val chunks = arg.split("/")
                    val current = cwdPath.chunks.toMutableList()

                    for (chunk in chunks) {
                        when (chunk) {
                            ".." -> current.removeAt(current.size - 1)
                            else -> current.add(chunk)
                        }
                    }

                    Path(current)
                }
            }
            val newCwd = fs[path]
            require(newCwd is Directory)
            cwdPath = path
            cwd = newCwd

        }
    }

    fun tryParseCommand(line: String): Cmd? {
        val cdMatch = CD_RE.matchEntire(line)

        if (cdMatch != null) {
            return Cd(cdMatch.groups[1]!!.value)
        }

        if (line.matches(LS_RE)) {
            return Ls()
        }

        return null
    }

    fun consumeLines(lines: Sequence<String>) {
        var currentCmd: Cmd? = null
        for (line in lines) {
            val nextCmd = tryParseCommand(line)
            if (nextCmd != null) {
                currentCmd?.run()
                currentCmd = nextCmd
            } else {
                val c = currentCmd
                require(c != null)
                c.consumeOutput(line)
            }
        }

        currentCmd?.run()
    }

    companion object {
        private val CD_RE = """\$\s+cd\s+(.*)""".toRegex()
        private val LS_RE = """\$\s+ls""".toRegex()
        private val LS_ITEM_RE = """(.*?)\s+(.*)""".toRegex()
        private val DIR = "dir"
    }
}

fun getSumOfDirsLessThan(sequence: Sequence<String>, limit: Int = 100000): Int {
    val console = Console()
    console.consumeLines(sequence)
    return console.fs.lsR().asSequence().map { it.fsItem }.filterIsInstance<Directory>().filter { it.size() <= limit }.sumOf { it.size() }
}

fun day7Part1() {
    println("day7/part1/console: ${
        getSumOfDirsLessThan(getResourceAsStream("/ski.gagar.aoc.aoc2022.day7/console.txt").bufferedReader().lineSequence())
    }")
}
