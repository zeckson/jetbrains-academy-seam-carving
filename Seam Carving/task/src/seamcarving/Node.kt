package seamcarving

import java.util.*

open class Node<T>(val value: T, comparator: Comparator<Node<T>>?) {
    val children = PriorityQueue(3, comparator)

    fun add(node: Node<T>) {
        children.add(node)
    }

}

fun <T> bfs(parent: Node<T>, visitor: (Node<T>) -> Unit) {
    val queue = LinkedList<Node<T>>()
    queue.push(parent)
    while (queue.isNotEmpty()) {
        val first = queue.pollFirst()
        visitor(first)
        for (child in first.children) {
            queue.addFirst(child)
        }
    }
}
