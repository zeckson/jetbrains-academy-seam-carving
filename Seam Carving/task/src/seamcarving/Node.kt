package seamcarving

import java.util.*

open class Node<T : Comparable<T>>(val value: T) : Comparable<Node<T>> {
    val children = PriorityQueue<Node<T>>(3)

    fun add(node: Node<T>) {
        children.add(node)
    }

    override fun compareTo(other: Node<T>): Int = this.value.compareTo(other.value)

}

fun <T : Comparable<T>> bfs(parent: Node<T>, visitor: (Node<T>) -> Unit) {
    val queue = LinkedList<Node<T>>()
    queue.push(parent)
    while (queue.isNotEmpty()) {
        val first = queue.pollFirst()
        visitor(first)
        for (child in first.children) {
            queue.addLast(child)
        }
    }
}

fun testBFS() {
    val root = Node("root")
    root.add(Node("a"))
    val b = Node("b")
    b.add(Node("d"))
    b.add(Node("e"))
    root.add(b)
    val c = Node("c")
    c.add(Node("f"))
    c.add(Node("g"))
    root.add(c)
    bfs(root) { log(it.value) }
}
