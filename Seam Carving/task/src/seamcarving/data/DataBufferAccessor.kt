package seamcarving.data

import seamcarving.exception.OutOfBufferException
import seamcarving.log
import java.awt.image.DataBuffer

typealias Coordinate = Pair<Int, Int>

abstract class DataBufferAccessor<T>(
    val buffer: DataBuffer,
    val width: Int,
    val height: Int,
    private val dataLength: Int = 1
) {
    init {
        log("Buffer size is ${buffer.size}")
    }

    fun get(coords: Coordinate): T = get(coords.first, coords.second)
    fun set(coords: Coordinate, value: T) = set(coords.first, coords.second, value)

    abstract fun set(x: Int, y: Int, value: T)
    abstract fun get(x: Int, y: Int): T

    fun forEach(fn: (x: Int, y: Int) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                fn(x, y)
            }
        }
    }

    fun forEach(fn: (coords: Coordinate) -> Unit) = forEach { x, y -> fn(seamcarving.data.Coordinate(x, y)) }

    protected fun offset(x: Int, y: Int): Int {
        val start = (y * width + x) * dataLength
        if (start > buffer.size) {
            throw OutOfBufferException(buffer.size, x, y)
        }
        return start
    }

    fun printToString(): String {
        val builder = StringBuilder()
        for (y in 0 until height) {
            for (x in 0 until width) {
                builder.append(get(x, y))
                builder.append(",")
            }
            builder.append("\n")
        }
        return builder.toString()
    }
}