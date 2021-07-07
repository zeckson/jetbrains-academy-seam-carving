package seamcarving

import kotlin.math.sign

data class Pixel(val coords: Pair<Int, Int>, val energy: Double) : Comparable<Pixel> {
    override fun compareTo(other: Pixel): Int = sign(this.energy - other.energy).toInt()
    override fun toString(): String {
        return "([$coords] - $energy)"
    }
}