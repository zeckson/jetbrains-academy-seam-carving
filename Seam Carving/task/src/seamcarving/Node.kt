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
        val children = PriorityQueue(first.children)
        while (children.isNotEmpty()) {
            queue.addLast(children.poll())
        }
    }
}

fun testBFS() {
    val root = Node("root")
    val b = Node("b")
    b.add(Node("d"))
    b.add(Node("e"))
    val c = Node("c")
    c.add(Node("f"))
    c.add(Node("g"))
    root.add(c)
    root.add(b)
    root.add(Node("a"))
    bfs(root) { log(it.value) }
}
