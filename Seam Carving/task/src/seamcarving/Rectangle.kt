package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun toRGB(color: Int): Triple<Int, Int, Int> {
    val blue = color and 0xFF
    val green = (color shr 8) and 0xFF
    val red = (color shr 16) and 0xFF
//  val alpha = (color shr 24) and 0xFF
    return Triple(red, green, blue)
}

private fun toInt(rgb: Triple<Int, Int, Int>): Int {
    return (0xFF shl 24) or (rgb.first shl 16) or (rgb.second shl 8) or rgb.third
}


fun pixel(data: Int): String = Integer.toBinaryString(data)

class RGBPrinter(private val data: IntArray, private val width: Int, private val height: Int) {
    fun negate(): IntArray {
        val negative = IntArray(data.size)
        for (idx in data.indices) {
            val rgb = toRGB(data[idx])
            negative[idx] = toInt(Triple(255 - rgb.first, 255 - rgb.second, 255 - rgb.third))
        }
        return negative
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        var first = true
        for (w in 0 until this.width) {
            if (!first) {
                buffer.append("\n")
            }
            first = false
            for (h in 0 until this.height) {
                val d = data[w * width + h]
                buffer.append(toRGB(d))
                if (this.height - h != 1) {
                    buffer.append(",")
                }
            }

        }
        return buffer.toString()
    }
}


private fun notMain() {
    println("Enter rectangle width:")
    val width = readLine()!!.toInt()
    println("Enter rectangle height:")
    val height = readLine()!!.toInt()
    println("Enter output image name:")
    val imageName = readLine()!!

    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.graphics
    graphics.color = Color.RED // Color(252, 13, 27)
    graphics.drawLine(0, 0, width - 1, height - 1)
    graphics.drawLine(0, height - 1, width - 1, 0)

    ImageIO.write(bufferedImage, "png", File(imageName))
    println("image $imageName created")
}
