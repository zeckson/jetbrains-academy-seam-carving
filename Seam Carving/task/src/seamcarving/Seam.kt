package seamcarving

import java.util.*

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

data class Score(val pixel: Pixel, val score: Double)

fun dijkstra(root: Node<Pixel>) {
    val queue = LinkedList<Node<Pixel>>()
    queue.push(root)
    while (queue.isNotEmpty()) {
        val first = queue.pollFirst()
        Score(first.value, first.value.energy)
        val children = PriorityQueue(first.children)
        while (children.isNotEmpty()) {
            queue.addLast(children.poll())
        }
    }
}