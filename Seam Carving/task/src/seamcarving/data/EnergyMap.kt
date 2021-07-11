package seamcarving.data

import java.awt.image.DataBufferDouble

class EnergyMap(width: Int, height: Int) :
    DataBufferAccessor(DataBufferDouble(width * height), width, height) {

    fun set(x: Int, y: Int, value: Double) {
        buffer.setElemDouble(offset(x, y), value)
    }

}