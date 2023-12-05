import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day  -----")

    val puzzleInput = getPuzzleInput()

    val seeds = getSeeds(puzzleInput)

    val locations = mapSeedsToLocation(seeds, puzzleInput)

    println("Task 1: Minimum location: ${locations.minOrNull()}")

    println("----------------------------------------")
}

fun mapSeedsToLocation(seeds: List<Long>, puzzleInput: List<String>): List<Long> {
    val seedToSoilMap = getMapByName("seed-to-soil map", puzzleInput)
    val soilToFertilizerMap = getMapByName("soil-to-fertilizer map", puzzleInput)
    val fertilizerToWaterMap = getMapByName("fertilizer-to-water map", puzzleInput)
    val waterToLightMap = getMapByName("water-to-light map", puzzleInput)
    val lightToTemperatureMap = getMapByName("light-to-temperature map", puzzleInput)
    val temperatureToHumidityMap = getMapByName("temperature-to-humidity map", puzzleInput)
    val humidityToLocationMap = getMapByName("humidity-to-location map", puzzleInput)

    return seeds.asSequence()
        .map(seedToSoilMap::getByInput)
        .map(soilToFertilizerMap::getByInput)
        .map(fertilizerToWaterMap::getByInput)
        .map(waterToLightMap::getByInput)
        .map(lightToTemperatureMap::getByInput)
        .map(temperatureToHumidityMap::getByInput)
        .map(humidityToLocationMap::getByInput)
        .toList()
}

fun getMapByName(delimiter: String, puzzleInput: List<String>): AlmanachMap {
    for (lineNumber in puzzleInput.indices) {
        if (puzzleInput[lineNumber].contains(delimiter)) {
            val map = mutableListOf<List<Long>>()
            var lineDela = lineNumber + 1

            while (puzzleInput[lineDela].isNotEmpty() && lineDela < puzzleInput.size) {
                val line = puzzleInput[lineDela]
                val row = line.split(" ")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { it.toLong() }
                map.add(row)

                lineDela++
            }

            return AlmanachMap(map)
        }
    }
    throw Exception("Map not found")
}

fun getSeeds(puzzleInput: List<String>): List<Long> {
    return puzzleInput[0]
        .substringAfter("seeds: ")
        .split(" ")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toLong() }
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class AlmanachMap(val map: List<List<Long>>) {
    fun getByInput(input: Long): Long {
        for (row in map) {
            if (valueIsInRange(input, row)) {
                return row[0] + (input - row[1])
            }
        }
        throw Exception("Input not found")
    }

    private fun valueIsInRange(input: Long, row: List<Long>): Boolean {
        val min = row[1]
        val max = (row[1] + row[2]) - 1

        return input in min..max
    }
}
