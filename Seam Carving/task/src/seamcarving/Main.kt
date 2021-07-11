package seamcarving

import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
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

    val lowestKey: Pixel? = findMinimal(scoreMap, inAccessor)
    if (lowestKey != null) {
        traceBack(scoreMap, lowestKey, outAccessor)
    }

    return outAccessor
}

private fun findMinimal(
    scoreMap: HashMap<Pixel, Score>,
    inAccessor: DataAccessor
): Pixel? {
    var lowestKey: Pixel? = null
    var lowest = Double.MAX_VALUE
    for (el in scoreMap) {
        val pixel = el.key
        val (_, y) = pixel.coords
        if (y == inAccessor.height - 1) {
            val score = el.value.score
            if (score < lowest) {
                lowest = score
                lowestKey = pixel
            }
        }
    }
    return lowestKey
}

private fun traceBack(
    scoreMap: HashMap<Pixel, Score>,
    lowestKey: Pixel,
    outAccessor: DataAccessor
) {
    val score = scoreMap[lowestKey]
    if (score != null) {
        val (x, y) = lowestKey.coords
        outAccessor.setPixel(x, y, RED)
        var parent = score.parent
        while (parent != null) {
            val coords = parent.node.value.coords
            outAccessor.setPixel(coords.first, coords.second, RED)
            parent = parent.parent
        }
    }
    log("$lowestKey ${score?.score}")
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


