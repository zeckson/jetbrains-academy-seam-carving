package seamcarving

import seamcarving.data.DataAccessor
import seamcarving.data.EnergyMapBuilder
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    val input = args[1]
    val output = args[3]

    val image = ImageIO.read(File(input))
    val originalRaster = image.raster
    val copyRaster = image.copyData(null)

    val width = image.width
    val height = image.height

    val accessor = buildSeam(
        DataAccessor(originalRaster.dataBuffer, width, height),
        DataAccessor(copyRaster.dataBuffer, width, height)
    )

    image.data = Raster.createWritableRaster(originalRaster.sampleModel, accessor.buffer, null)

    ImageIO.write(image, "png", File(output))
}

private fun buildSeam(
    inAccessor: DataAccessor,
    outAccessor: DataAccessor,
): DataAccessor {
    val energyMap = EnergyMapBuilder.createVerticalSeamsMap(inAccessor)

    log(energyMap.printToString())

    val end = energyMap.getLowestEnergy(energyMap.height - 1)

    log("End: $end")

    energyMap.traceback(end) {
        log("$it")
        outAccessor.set(it, RED)
    }

    return outAccessor
}

private fun buildEnergyMap(
    inAccessor: DataAccessor,
    outAccessor: DataAccessor
): DataAccessor {
    val energyMap = EnergyMapBuilder.createEnergyMap(inAccessor)

    energyMap.forEach { x, y ->
        val energy = energyMap.get(x, y)
        val intensity = (255.0 * energy / energyMap.maxValue).toInt()
        outAccessor.set(x, y, RGB(intensity, intensity, intensity))
    }

    return outAccessor
}



