package seamcarving

import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

val LOG = false;

data class RGB(val r: Int, val g: Int, val b: Int)

fun gradient(left: RGB, right: RGB): Double {
    var result = (left.r - right.r).toDouble().pow(2)
    result += (left.g - right.g).toDouble().pow(2)
    result += (left.b - right.b).toDouble().pow(2)
    return result
}

class DataAccessor(val buffer: DataBuffer, val width: Int, val height: Int) {
    init {
        if (LOG) {
            println("Buffer size is $buffer.size")
        }
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

    fun forEach(fn: (x: Int, y: Int) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                fn(x, y)
            }
        }
    }

    private fun offset(y: Int, x: Int): Int {
        val start = (y * width + x) * 3
        if (LOG) {
            println("$x, $y = $start")
        }
        if (start > buffer.size) {
            throw OutOfBufferException(size = buffer.size, x, y)
        }
        return start
    }
}

fun main(args: Array<String>) {
    val input = args[1]
    val output = args[3]

    val image = ImageIO.read(File(input))
    val originalRaster = image.raster
    val buffer = originalRaster.dataBuffer

    val width = image.width
    val height = image.height
    val accessor = DataAccessor(buffer, width, height)

    var maxEnergy = 0.0
    accessor.forEach { x, y ->
        val energy = accessor.getEnergy(x, y)
        if (energy > maxEnergy) {
            maxEnergy = energy
        }
    }

    val energyAccessor = DataAccessor(DataBufferByte(buffer.size), width, height)
    accessor.forEach { x, y ->
        val energy = accessor.getEnergy(x, y)
        val intensity = (255.0 * energy / maxEnergy).toInt()
        energyAccessor.setPixel(x, y, RGB(intensity, intensity, intensity))
    }

    image.data = Raster.createWritableRaster(originalRaster.sampleModel, energyAccessor.buffer, null)

    ImageIO.write(image, "png", File(output))
}

