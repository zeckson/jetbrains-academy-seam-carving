package seamcarving

import seamcarving.data.DataAccessor
import seamcarving.data.EnergyMap
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

    val accessor = findSeam2(
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

private fun findSeam2(
    inAccessor: DataAccessor,
    outAccessor: DataAccessor,
): DataAccessor {
    val energyMap = EnergyMapBuilder.createVerticalSeamsMap(inAccessor)

    log(energyMap.printToString())

    val end = energyMap.getLowestEnergy(energyMap.height - 1)

    log("End: $end")

    traceback(end, energyMap) {
        log("$it")
        outAccessor.set(it, RED)
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
    inAccessor: DataAccessor,
    outAccessor: DataAccessor
): DataAccessor {
    val energyMap = createEnergyMap(inAccessor)

    energyMap.forEach { x, y ->
        val energy = energyMap.get(x, y)
        val intensity = (255.0 * energy / energyMap.maxValue).toInt()
        outAccessor.setPixel(x, y, RGB(intensity, intensity, intensity))
    }

    return outAccessor
}

private fun createEnergyMap(inAccessor: DataAccessor): EnergyMap {
    val energyMap = EnergyMap(inAccessor.width, inAccessor.height)
    inAccessor.forEach { x, y ->
        energyMap.set(x, y, inAccessor.getEnergy(x, y))
    }
    return energyMap
}


