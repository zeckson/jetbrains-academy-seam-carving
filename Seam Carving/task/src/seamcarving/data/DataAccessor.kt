package seamcarving.data

import seamcarving.RGB
import java.awt.image.DataBuffer
import kotlin.math.sqrt

class DataAccessor(buffer: DataBuffer, width: Int, height: Int) :
    DataBufferAccessor<RGB>(buffer, width, height, dataLength = 3) {
    fun getEnergy(coords: Pair<Int, Int>): Double = getEnergy(coords.first, coords.second)

    fun getEnergy(x: Int, y: Int): Double {
        val pointX = when (x) {
            0 -> x + 1
            width - 1 -> width - 2
            else -> x
        }
        val pointY = when (y) {
            0 -> y + 1
            height - 1 -> height - 2
            else -> y
        }

        val gradientX = get(pointX - 1, y).gradient(get(pointX + 1, y))
        val gradientY = get(x, pointY - 1).gradient(get(x, pointY + 1))

        return sqrt(gradientX + gradientY)
    }

    override fun set(x: Int, y: Int, value: RGB) {
        val start = offset(x, y)
        buffer.setElem(start, value.b)
        buffer.setElem(start + 1, value.g)
        buffer.setElem(start + 2, value.r)
    }

    override fun get(x: Int, y: Int): RGB {
        val start = offset(x, y)
        return RGB(buffer.getElem(start + 2), buffer.getElem(start + 1), buffer.getElem(start))
    }

    override fun newEmptyCopy(): DataBufferAccessor<RGB> = newEmptyAccessor(width, height)

    companion object {
        fun newEmptyAccessor(width: Int, height: Int) =
            DataAccessor(newByteBuffer(width, height), width, height)
    }

    override fun print(x: Int, y: Int): String = get(x, y).toString()

}