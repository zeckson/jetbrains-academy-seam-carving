package seamcarving

import java.util.*
import kotlin.math.sign

data class Pixel(val coords: Pair<Int, Int>, val energy: Double) : Comparable<Pixel> {
    override fun compareTo(other: Pixel): Int = sign(this.energy - other.energy).toInt()
}

open class Node<T>(val value: T, comparator: Comparator<Node<T>>?) {
    private val queue = PriorityQueue(3, comparator)

    fun add(node: Node<T>) {
        queue.add(node)
    }
}

// TODO: fix generic Node<Pixel> -> PixelNode
val PIXEL_COMPARATOR: (o1: Node<Pixel>, o2: Node<Pixel>) -> Int = { left, right ->
    sign(left.value.energy - right.value.energy).toInt()
}

class PixelNode(pixel: Pixel) : Node<Pixel>(pixel, PIXEL_COMPARATOR)

class TreeBuilder(private val accessor: DataAccessor) {
    private val pixelCache = HashMap<Pair<Int, Int>, Pixel>()


    fun buildTree(startX: Int): PixelNode {
        val pixel =
            getPixel(Pair(startX, 0)) ?: throw IllegalArgumentException("Start pixel not found for [$startX, 0]")
        val parent = PixelNode(pixel)
        log("Root [${parent.value.coords}]")
        next(parent)
        return parent
    }

    private fun next(parent: PixelNode) {
        val (x, y) = parent.value.coords.let { Pair(it.first, it.second + 1) }
        if (y == accessor.height) return
        when {
            x < 0 -> return
            x >= accessor.width -> return
        }
        val pixels = PriorityQueue<Pixel>(3)

        getPixel(Pair(x - 1, y))?.let { pixels.add(it) }
        getPixel(Pair(x, y))?.let { pixels.add(it) }
        getPixel(Pair(x + 1, y))?.let { pixels.add(it) }

        for (pixel in pixels) {
            val node = PixelNode(pixel)
            log("Parent [${parent.value.coords}] -> [${node.value.coords}]")
            parent.add(node)
            next(node)
        }
    }

    private fun getPixel(coords: Pair<Int, Int>): Pixel? {
        val (x, y) = coords
        when {
            x < 0 -> return null
            x >= accessor.width -> return null
        }
        var pixel = pixelCache[coords]
        if (pixel == null) {
            pixel = Pixel(coords, accessor.getEnergy(x, y))
            pixelCache[coords] = pixel
        }
        return pixel
    }

}