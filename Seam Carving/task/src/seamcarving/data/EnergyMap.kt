package seamcarving.data

import seamcarving.log
import java.awt.image.DataBufferDouble
import java.lang.StringBuilder

typealias Energy = Double

class EnergyMap(width: Int, height: Int) :
    DataBufferAccessor<Energy>(DataBufferDouble(width * height), width, height) {
    var maxValue: Energy = 0.0

    override fun set(x: Int, y: Int, value: Energy) {
        if (value > maxValue) {
            maxValue = value
        }
        buffer.setElemDouble(offset(x, y), value)
    }

    override fun get(x: Int, y: Int): Energy = buffer.getElemDouble(offset(x, y))

    fun lowest(coordinate: Coordinate): Coordinate {
        val (x,y) = coordinate
        var result = coordinate
        var lowest = get(coordinate)

        val logInfo  = StringBuilder()

        val leftX = x - 1
        if (leftX >= 0) {
            val left = get(leftX, y)
            logInfo.append("[$leftX, $y] = $left, ")
            if (left < lowest) {
                lowest = left
                result = Coordinate(leftX, y)
            }
        }
        logInfo.append("[$coordinate] = ${get(coordinate)}")
        val rightX = x + 1
        if (rightX < width) {
            val right = get(rightX, y)
            logInfo.append(", [$rightX, $y] = $right")
            if (right < lowest) {
                result = Coordinate(rightX, y)
            }
        }

        log(logInfo.toString())
        log("selected = ${result.first - x}, c: [$result]")
        return result

    }

    fun buildSeam() {

    }


    fun getLowestEnergy(line: Int): Coordinate {
        var lowestEnergy = Double.MAX_VALUE
        var lowestX = 0

        // Find lowest energy start node
        for (x in 0 until this.width) {
            val energy = this.get(x, line)
            if (energy < lowestEnergy) {
                lowestEnergy = energy
                lowestX = x
            }
        }

        return Coordinate(lowestX, line)
    }

}