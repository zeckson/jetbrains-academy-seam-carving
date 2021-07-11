package seamcarving.data

import seamcarving.RGB
import seamcarving.log
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

        val gradientX = getPixel(pointX - 1, y).gradient(getPixel(pointX + 1, y))
        val gradientY = getPixel(x, pointY - 1).gradient(getPixel(x, pointY + 1))

        return sqrt(gradientX + gradientY)
    }

    fun getPixel(x: Int, y: Int): RGB {
        val start = offset(x, y)
        return RGB(buffer.getElem(start + 2), buffer.getElem(start + 1), buffer.getElem(start))
    }

    fun setPixel(x: Int, y: Int, value: RGB) {
        val start = offset(x, y)
        buffer.setElem(start, value.b)
        buffer.setElem(start + 1, value.g)
        buffer.setElem(start + 2, value.r)
    }

    fun lowestX(x: Int, y: Int): Int {
        if (y == 2) {
            log("[6, 2] = ${getEnergy(6, 2)}")
            log("[7, 2] = ${getEnergy(7, 2)}")
            log("[8, 2] = ${getEnergy(8, 2)}")
        }
        val center = getEnergy(x, y)
        var left = center
        val leftX = x - 1
        if (leftX >= 0) {
            left = getEnergy(leftX, y)
        }
        var right = center
        val rightX = x + 1
        if (rightX < width) {
            right = getEnergy(rightX, y)
        }
        val out = when {
            left < center && left < right -> leftX
            right < center && right < left -> rightX
            else -> x
        }
        log("[$leftX, $y] = $left, [$x, $y] = $center, [$rightX, $y] = $right")
        log("selected = ${out - x}, c: [$out, $y]")
        return out

    }

    override fun set(x: Int, y: Int, value: RGB) = setPixel(x, y, value)

    override fun get(x: Int, y: Int): RGB = getPixel(x, y)

}