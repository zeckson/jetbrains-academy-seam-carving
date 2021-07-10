package seamcarving

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


fun dijkstra(root: Node<Pixel>): HashMap<Node<Pixel>, Score> {
    val unprocessed = LinkedList<Score>()
    val visited = HashSet<Node<Pixel>>()
    val scoredMap = HashMap<Node<Pixel>, Score>()

    val initial = Score(root, 0.0)
    unprocessed.push(initial)
    visited.add(root)
    scoredMap[root] = initial
    while (unprocessed.isNotEmpty()) {
        val parent = unprocessed.pollFirst()

        val children = PriorityQueue(parent.node.children)
        while (children.isNotEmpty()) {
            val child = children.poll()
            if (visited.contains(child)) continue

            val pixel = child.value
            val score = parent.score + pixel.energy
            var scored = scoredMap[child]
            if (scored == null) {
                scored = Score(child)
                scoredMap[child] = scored
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