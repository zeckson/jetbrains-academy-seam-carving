package seamcarving

import seamcarving.data.DataAccessor
import seamcarving.data.EnergyMapBuilder
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    val inputName = args[1]
    val outputName = args[3]

    val image = ImageIO.read(File(inputName))
    val originalRaster = image.raster

    val width = image.width
    val height = image.height

    val accessor = buildSeam(
        DataAccessor(originalRaster.dataBuffer, width, height),
    )

    val out = BufferedImage(accessor.width, accessor.height, image.type)
    out.data = Raster.createWritableRaster(originalRaster.sampleModel, accessor.buffer, null)

    ImageIO.write(out, "png", File(outputName))
}

private fun buildSeam(
    inAccessor: DataAccessor,
): DataAccessor {
    val energyMap = EnergyMapBuilder.createVerticalSeamsMap(inAccessor)

    log(energyMap.printToString())

    val end = energyMap.getLowestEnergy(energyMap.height - 1)

    log("End: $end")

    val outAccessor = inAccessor.copy() as DataAccessor

    energyMap.traceback(end) {
        log("$it")
        outAccessor.set(it, RED)
    }

    return outAccessor
}

private fun rotate(
    inAccessor: DataAccessor,
): DataAccessor {
    val size = 3
    val result = DataAccessor(
        DataBufferByte(inAccessor.width * inAccessor.height * size),
        inAccessor.height,
        inAccessor.width,
    )
    inAccessor.forEach { (x, y) -> result.set(y, x, inAccessor.get(x, y)) }

    return result
}

private fun buildEnergyMap(
    inAccessor: DataAccessor,
): DataAccessor {
    val energyMap = EnergyMapBuilder.createEnergyMap(inAccessor)

    val outAccessor = DataAccessor.newEmptyAccessor(inAccessor.width, inAccessor.height)

    energyMap.forEach { x, y ->
        val energy = energyMap.get(x, y)
        val intensity = (255.0 * energy / energyMap.maxValue).toInt()
        outAccessor.set(x, y, RGB(intensity, intensity, intensity))
    }

    return outAccessor
}



