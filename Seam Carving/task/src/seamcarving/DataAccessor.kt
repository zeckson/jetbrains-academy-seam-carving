package seamcarving

import java.awt.image.DataBuffer
import kotlin.math.pow
import kotlin.math.sqrt

fun gradient(left: RGB, right: RGB): Double {
    var result = (left.r - right.r).toDouble().pow(2)
    result += (left.g - right.g).toDouble().pow(2)
    result += (left.b - right.b).toDouble().pow(2)
    return result
}


class DataAccessor(val buffer: DataBuffer, val width: Int, val height: Int) {
    init {
        log("Buffer size is $buffer.size")
    }

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

        val gradientX = gradient(getPixel(pointX - 1, y), getPixel(pointX + 1, y))
        val gradientY = gradient(getPixel(x, pointY - 1), getPixel(x, pointY + 1))

        return sqrt(gradientX + gradientY)
    }

    fun getPixel(x: Int, y: Int): RGB {
        val start = offset(y, x)
        return RGB(buffer.getElem(start + 2), buffer.getElem(start + 1), buffer.getElem(start))
    }

    fun setPixel(x: Int, y: Int, value: RGB) {
        val start = offset(y, x)
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

    fun forEach(fn: (x: Int, y: Int) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                fn(x, y)
            }
        }
    }

    private fun offset(y: Int, x: Int): Int {
        val start = (y * width + x) * 3
//        log("$x, $y = $start")
        if (start > buffer.size) {
            throw OutOfBufferException(size = buffer.size, x, y)
        }
        return start
    }
}