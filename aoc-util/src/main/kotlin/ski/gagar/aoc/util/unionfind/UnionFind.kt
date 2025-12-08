package ski.gagar.aoc.util.unionfind

class UnionFind(private val size: Int) {
    private val ids = mutableMapOf<Int, Int>()
    var count = size
        private set

    fun find(p: Int): Int {
        require(p < size)
        return ids[p] ?: p
    }

    fun union(p: Int, q: Int) {
        require(p < size)
        require(q < size)

        val pId = ids[p] ?: p
        val qId = ids[q] ?: q

        if (pId == qId) return

        ids[pId] = qId

        for ((k, v) in ids) {
            if (v == pId) ids[k] = qId
        }

        count--
    }

    fun connected(p: Int, q: Int): Boolean {
        require(p < size)
        require(q < size)
        return (ids[p] ?: p) == (ids[q] ?:q)
    }
}