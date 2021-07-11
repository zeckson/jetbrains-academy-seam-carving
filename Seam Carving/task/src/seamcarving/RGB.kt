package seamcarving

import kotlin.math.pow

val RED = RGB(255, 0, 0)
enum class Color(val rgb: RGB) {
    RED(seamcarving.RED)
}


data class RGB(val r: Int, val g: Int, val b: Int) {
    fun gradient(to: RGB): Double {
        var result = (this.r - to.r).toDouble().pow(2)
        result += (this.g - to.g).toDouble().pow(2)
        result += (this.b - to.b).toDouble().pow(2)
        return result
    }
}