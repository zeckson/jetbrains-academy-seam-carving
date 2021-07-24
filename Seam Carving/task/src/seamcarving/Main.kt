package seamcarving

import seamcarving.data.Coordinate
import seamcarving.data.DataAccessor
import seamcarving.data.EnergyMapBuilder
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    val inputName = args[1]
    val outputName = args[3]
    val vertical = (args.size > 4).let { args[5] }
    val horizontal = (args.size > 4).let { args[7] }

    val image = ImageIO.read(File(inputName))
    val originalRaster = image.raster

    val width = image.width
    val height = image.height
    val source = DataAccessor(originalRaster.dataBuffer, width, height)

    val target = horizontalSeam(source)

    val out = BufferedImage(target.width, target.height, image.type)
    out.data = Raster.createWritableRaster(
        originalRaster.sampleModel.createCompatibleSampleModel(
            target.width,
            target.height
        ), target.buffer, null
    )

    ImageIO.write(out, "png", File(outputName))
}

private fun horizontalSeam(
    inAccessor: DataAccessor,
): DataAccessor {
    val right: DataAccessor.(source: Coordinate) -> Coordinate = { (x, y) -> Coordinate(y, x) }
    val rightRotate = rotate(inAccessor, right)
    val seam = verticalSeam(rightRotate)
    return rotate(seam, right)
}

private fun verticalSeam(
    inAccessor: DataAccessor,
): DataAccessor {
    val energyMap = EnergyMapBuilder.createVerticalSeamsMap(inAccessor)

    val seam = energyMap.getSeam()

    log(seam.joinToString(","))

    return inAccessor.copy(seam) as DataAccessor
}

private fun rotate(
    inAccessor: DataAccessor,
    transform: DataAccessor.(source: Coordinate) -> Coordinate
): DataAccessor {
    val width = inAccessor.height
    val height = inAccessor.width

    val out = DataAccessor.newEmptyAccessor(width, height)

    inAccessor.forEach { it -> out.set(out.transform(it), inAccessor.get(it)) }

    return out
}

private fun copy(
    inAccessor: DataAccessor,
): DataAccessor {

    val result = DataAccessor.newEmptyAccessor(inAccessor.width, inAccessor.height)

    inAccessor.forEach { (x, y) -> result.set(x, y, inAccessor.get(x, y)) }

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



