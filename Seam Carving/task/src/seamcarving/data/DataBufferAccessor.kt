package seamcarving.data

import seamcarving.exception.OutOfBufferException
import seamcarving.log
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte

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

    abstract fun newEmptyCopy(width: Int = this.width, height: Int = this.height): DataBufferAccessor<T>

    fun forEach(fn: (x: Int, y: Int) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
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
                builder.append(print(x, y))
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    protected open fun print(x: Int, y: Int) = "%6.2f,".format(get(x, y))

    fun copy(): DataBufferAccessor<out T> {
        val result = newEmptyCopy()
        forEach { it ->
            result.set(it, this.get(it))
        }
        return result
    }

    fun copy(except: IntArray): DataBufferAccessor<out T> {
        val result = newEmptyCopy(this.width - 1, this.height)
        forEach { (x, y) ->
            val exclude = except[y]
            when {
                x > exclude -> result.set(x - 1, y, get(x, y))
                x < exclude -> result.set(x, y, get(x, y))
            }
        }
        return result
    }

    companion object {
        fun newByteBuffer(width: Int, height: Int) = DataBufferByte(width * height * 3)
    }
}