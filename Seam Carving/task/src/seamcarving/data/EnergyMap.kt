package seamcarving.data

import java.awt.image.DataBufferDouble

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
}