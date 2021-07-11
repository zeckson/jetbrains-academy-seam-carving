package seamcarving.data

object EnergyMapBuilder {
    fun createEnergyMap(accessor: DataAccessor): EnergyMap {
        val energyMap = EnergyMap(accessor.width, accessor.height)
        accessor.forEach { x, y ->
            energyMap.set(x, y, accessor.getEnergy(x, y))
        }
        return energyMap
    }

    fun createVerticalSeamsMap(accessor: DataAccessor): EnergyMap {
        val energyMap = EnergyMap(accessor.width, accessor.height)
        accessor.forEach { coords: Coordinate ->
            val parentEnergy = energyMap.getLowestParentEnergy(coords)
            val energy = accessor.getEnergy(coords) + parentEnergy
            energyMap.set(coords, energy)
        }
        return energyMap
    }

}