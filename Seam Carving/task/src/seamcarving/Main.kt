package seamcarving

import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO

const val LOG = true
fun log(value: String) {
    if (LOG) {
        println(value)
    }
}

val RED = RGB(255, 0, 0)
fun main(args: Array<String>) {
    val input = args[1]
    val output = args[3]

    val image = ImageIO.read(File(input))
    val originalRaster = image.raster
    val copyRaster = image.copyData(null)

    val width = image.width
    val height = image.height

//    testBFS()

    val accessor = findSeam(
        DataAccessor(originalRaster.dataBuffer, width, height),
        DataAccessor(copyRaster.dataBuffer, width, height)
    )

    image.data = Raster.createWritableRaster(originalRaster.sampleModel, accessor.buffer, null)

    ImageIO.write(image, "png", File(output))
}

private fun findSeam(
    inAccessor: DataAccessor,
    outAccessor: DataAccessor,
): DataAccessor {
    val start = findStartPoint(inAccessor)

    val treeBuilder = TreeBuilder(inAccessor)
    val root = treeBuilder.buildTree(start)

    val scoreMap = dijkstra(root)

    findShortestPath(root, outAccessor, scoreMap)

    return outAccessor
}
private fun buildEnergyMap(
    buffer: DataBuffer,
    width: Int,
    height: Int,
): DataAccessor {
    val accessor = DataAccessor(buffer, width, height)
    var maxEnergy = 0.0
    accessor.forEach { x, y ->
        val energy = accessor.getEnergy(x, y)
        if (energy > maxEnergy) {
            maxEnergy = energy
        }
    }

    val energyAccessor = DataAccessor(DataBufferByte(buffer.size), width, height)
    accessor.forEach { x, y ->
        val energy = accessor.getEnergy(x, y)
        val intensity = (255.0 * energy / maxEnergy).toInt()
        energyAccessor.setPixel(x, y, RGB(intensity, intensity, intensity))
    }

    return energyAccessor
}


