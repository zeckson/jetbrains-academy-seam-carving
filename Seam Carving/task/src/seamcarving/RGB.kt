package seamcarving

val RED = RGB(255, 0, 0)
enum class Color(val rgb: RGB) {
    RED(seamcarving.RED)
}

data class RGB(val r: Int, val g: Int, val b: Int)