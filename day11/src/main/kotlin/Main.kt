package org.example

import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 10 -----")

    val puzzleInput = getPuzzleInput()
    val starMap = puzzleInput
        .map { line ->
            line.map { Space.fromChar(it) }
        }

    val spaceGraph = buildSpaceGraph(starMap)
    val shortestPathLength = getShortestPathLength(spaceGraph)

    println("Task 1: Shortest path length: $shortestPathLength")

    println("----------------------------------------")
}

fun getShortestPathLength(galaxies: List<Space>): Int {
    return getPathLength(findShortestPath(galaxies))
}


fun findShortestPath(galaxies: List<Space>): List<Space> {
    val visited = mutableSetOf<Space>()
    var currentSpace = galaxies.first()
    visited.add(currentSpace)

    val path = mutableListOf(currentSpace)

    while (visited.size < galaxies.size) {
        val nextSpace = currentSpace.connections
            .filter { it.key !in visited }
            .minByOrNull { it.value }
            ?.key ?: break

        visited.add(nextSpace)
        path.add(nextSpace)
        currentSpace = nextSpace
    }

    return path
}

fun getPathLength(path: List<Space>): Int {
    return path
        .zipWithNext()
        .sumOf { (current, next) -> current.getDistanceTo(next) }
}

fun buildSpaceGraph(starMap: List<List<Space>>): List<Space> {
    val galaxies = starMap.flatten().filter { it.isGalaxy }
    val distanceMap = buildMapWithMovement(starMap)

    require(starMap.size == distanceMap.size) { "Galaxies and distance map have different sizes" }
    for (line in distanceMap.indices) {
        require(starMap[line].size == distanceMap[line].size) { "Galaxies and distance map have different sizes" }
    }

    galaxies
        .map { galaxy ->
            val (x, y) = getGalaxyCoordinates(starMap, galaxy)

            for (otherGalaxy in galaxies) {
                if (otherGalaxy == galaxy) {
                    continue
                }

                val (otherX, otherY) = getGalaxyCoordinates(starMap, otherGalaxy)
                val distance = getDistance(distanceMap, x to y, otherX to otherY)

                galaxy.addConnection(otherGalaxy, distance)
            }

        }

    return galaxies
}

fun getGalaxyCoordinates(starMap: List<List<Space>>, galaxy: Space): Pair<Int, Int> {
    val galaxyLine = starMap.find { it.contains(galaxy) }!!
    val galaxyColumnIndex = galaxyLine.indexOf(galaxy)
    val galaxyLineIndex = starMap.indexOf(galaxyLine)
    return galaxyLineIndex to galaxyColumnIndex
}

fun getDistance(distanceMap: List<List<Int>>, start: Pair<Int, Int>, target: Pair<Int, Int>): Int {
    val (startX, startY) = start
    val (targetX, targetY) = target

    var distance = 0
    var currentX = startX
    var currentY = startY

    while (currentX != targetX) {
        val nextX = if (currentX < targetX) currentX + 1 else currentX - 1
        if (currentY in distanceMap.indices && nextX in distanceMap[currentY].indices) {
            distance += distanceMap[currentY][nextX]
        } else {
            error("Indexes out of bounds: $nextX, $currentY")
        }
        currentX = nextX
    }

    while (currentY != targetY) {
        val nextY = if (currentY < targetY) currentY + 1 else currentY - 1
        if (nextY in distanceMap.indices && currentX in distanceMap[nextY].indices) {
            distance += distanceMap[nextY][currentX]
        } else {
            error("Indexes out of bounds: $currentX, $nextY")
        }
        currentY = nextY
    }

    return distance


}

/*
corrects distances; lines and columns without Galaxies have double the width/height
 */
fun buildMapWithMovement(starMap: List<List<Space>>): List<List<Int>> {
    val mapWithMovement = mutableListOf<List<Int>>()
    for (line in starMap) {
        if (isEmptyArea(line)) {
            mapWithMovement.add(line.map { 2 })
        } else {
            mapWithMovement.add(line.map { 1 })
        }
    }

    for (column in starMap[0].indices) {
        val columnLine = starMap.map { it[column] }
        // if column is empty, double the distance
        if (isEmptyArea(columnLine)) {
            for (line in mapWithMovement.indices) {
                val lineList = mapWithMovement[line].toMutableList()
                lineList[column] *= 2
                mapWithMovement[line] = lineList
            }
        }

    }

    return mapWithMovement
}

fun isEmptyArea(line: List<Space>): Boolean {
    for (space in line) {
        if (space.isGalaxy) {
            return false
        }
    }

    return true
}


fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

class Space private constructor(val galaxyId: Int, val isEmpty: Boolean) {

    val connections = mutableMapOf<Space, Int>()
    val isGalaxy
        get() = !isEmpty

    fun addConnection(space: Space, distance: Int) {
        connections[space] = distance
    }

    fun getDistanceTo(space: Space): Int {
        return connections[space] ?: throw Exception("No connection to space $space")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Space

        return galaxyId == other.galaxyId
    }

    override fun hashCode(): Int {
        return galaxyId
    }

    companion object {
        private var nextGalaxyId = 0

        fun fromChar(char: Char): Space = when (char) {
            '#' -> Space(nextGalaxyId++, false)
            '.' -> Space(nextGalaxyId++, true)
            else -> error("Unknown space type: $char")
        }

    }
}
