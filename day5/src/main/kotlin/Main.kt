import java.io.File

var seedToSoilMap: AlmanachMap? = null
var soilToFertilizerMap: AlmanachMap? = null
var fertilizerToWaterMap: AlmanachMap? = null
var waterToLightMap: AlmanachMap? = null
var lightToTemperatureMap: AlmanachMap? = null
var temperatureToHumidityMap: AlmanachMap? = null
var humidityToLocationMap: AlmanachMap? = null

fun main() {
    println("------ Advent of Code 2023 - Day  -----")

    val puzzleInput = getPuzzleInput()

    val seeds = getSeeds(puzzleInput)

    seedToSoilMap = getMapByName("seed-to-soil map", puzzleInput)
    soilToFertilizerMap = getMapByName("soil-to-fertilizer map", puzzleInput)
    fertilizerToWaterMap = getMapByName("fertilizer-to-water map", puzzleInput)
    waterToLightMap = getMapByName("water-to-light map", puzzleInput)
    lightToTemperatureMap = getMapByName("light-to-temperature map", puzzleInput)
    temperatureToHumidityMap = getMapByName("temperature-to-humidity map", puzzleInput)
    humidityToLocationMap = getMapByName("humidity-to-location map", puzzleInput)

    val locations = seeds.asSequence()
        .map { seed -> mapSeedsToLocation(seed) }

    println("Task 1: Minimum location: ${locations.minOrNull()}")

    // --- Task 2 ---

    val minimumSeedLocation = findMinimumLocationForSeedRanges(seeds)

    println("Task 2: Minimum location: $minimumSeedLocation")

    println("----------------------------------------")
}

fun findMinimumLocationForSeedRanges(seeds: List<Long>): Long {
    return seeds
        .chunked(2)
        .fold(Long.MAX_VALUE) { minLocation, seedRange ->
            val (start, length) = seedRange
            val end = start + length

            (start..<end).fold(minLocation) { currentMin, seed ->
                minOf(currentMin, mapSeedsToLocation(seed))
            }
        }
}

fun mapSeedsToLocation(seed: Long): Long {

    return listOfNotNull(
        seedToSoilMap,
        soilToFertilizerMap,
        fertilizerToWaterMap,
        waterToLightMap,
        lightToTemperatureMap,
        temperatureToHumidityMap,
        humidityToLocationMap
    ).fold(seed) { input, map ->
        map.getByInput(input)
    }
}

fun getMapByName(delimiter: String, puzzleInput: List<String>): AlmanachMap {
    for (lineNumber in puzzleInput.indices) {
        if (puzzleInput[lineNumber].contains(delimiter)) {
            val map = mutableListOf<List<Long>>()
            var lineDela = lineNumber + 1

            // Stelle sicher, dass lineDela innerhalb der Grenzen von puzzleInput bleibt
            while (lineDela < puzzleInput.size && puzzleInput[lineDela].isNotEmpty()) {
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
        // if no row was found, return input
        return input
    }

    private fun valueIsInRange(input: Long, row: List<Long>): Boolean {
        val min = row[1]
        val max = (row[1] + row[2]) - 1

        return input in min..max
    }
}
