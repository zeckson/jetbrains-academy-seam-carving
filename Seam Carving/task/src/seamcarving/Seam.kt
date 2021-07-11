package seamcarving

import seamcarving.data.DataAccessor
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.sign

fun findStartPoint(accessor: DataAccessor): Pair<Int, Int> {
    var lowestEnergy = Double.MAX_VALUE
    var lowestX = 0

    // Find lowest energy start node
    for (x in 0..accessor.width) {
        val energy = accessor.getEnergy(x, 0)
        if (energy < lowestEnergy) {
            lowestEnergy = energy
            lowestX = x
        }
    }

    return Pair(lowestX, 0)
}

data class Score(val node: Node<Pixel>, var score: Double = Double.MAX_VALUE) : Comparable<Score> {
    var parent:Score? = null
    override fun compareTo(other: Score): Int {
        return sign(score - other.score).toInt()
    }
}

fun direct(inAccessor: DataAccessor, outAccessor: DataAccessor) {
    var (x, y) = findStartPoint(inAccessor)

    // go seam
    while (true) {
        outAccessor.setPixel(x, y, RED)
        ++y
        if (y == inAccessor.height) break
        x = inAccessor.lowestX(x, y)
    }
}

fun findShortestPath(
    start: PixelNode,
    outAccessor: DataAccessor,
    scoreMap: HashMap<Pixel, Score>
) {
    var current: Node<Pixel> = start
    while (true) {
        val (x, y) = current.value.coords
        outAccessor.setPixel(x, y, RED)

        var next = current
        var lowestScore = Double.MAX_VALUE
        for (node in current.children) {
            val score = scoreMap[node.value]
            if (score != null) {
                val currentScore = score.score
                if (currentScore < lowestScore) {
                    lowestScore = currentScore
                    next = node
                }
            }
        }
        log(current.value.toString() + "$lowestScore" )
        if (next == current) break

        current = next
    }
}

fun dijkstra(root: Node<Pixel>): HashMap<Pixel, Score> {
    val unprocessed = LinkedList<Score>()
    val visited = HashSet<Node<Pixel>>()
    val scoredMap = HashMap<Pixel, Score>()

    val initial = Score(root, 0.0)
    unprocessed.push(initial)
    visited.add(root)
    scoredMap[root.value] = initial
    while (unprocessed.isNotEmpty()) {
        val parent = unprocessed.pollFirst()

        val children = PriorityQueue(parent.node.children)
        while (children.isNotEmpty()) {
            val child = children.poll()
            if (visited.contains(child)) continue

            val pixel = child.value
            val score = parent.score + pixel.energy
            var scored = scoredMap[child.value]
            if (scored == null) {
                scored = Score(child)
                scoredMap[child.value] = scored
            }
            if (score < scored.score) {
                scored.score = score
                scored.parent = parent
                unprocessed.remove(scored)
                unprocessed.add(scored)
            }
        }
        visited.add(parent.node)
    }
    return scoredMap
}