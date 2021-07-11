package seamcarving.data

import seamcarving.exception.OutOfBufferException
import java.awt.image.DataBuffer

open class DataBufferAccessor(val buffer: DataBuffer, val width: Int, val height: Int) {
    fun forEach(fn: (x: Int, y: Int) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                fn(x, y)
            }
        }
    }

    protected fun offset(x: Int, y: Int): Int {
        val start = (y * width + x) * 3
        if (start > buffer.size) {
            throw OutOfBufferException(buffer.size, x, y)
        }
        return start
    }
}