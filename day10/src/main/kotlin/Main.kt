package org.example

import org.example.Direction.*
import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 10 -----")

    val puzzleInput = getPuzzleInput()
    val map: List<List<Tile>> = puzzleInput
        .map { it.toCharArray().map { symbol -> Tile(symbol) } }

    val loopLength = getLoopLength(map)

    println("Task 1: Steps to furthest point: ${loopLength / 2}")

    val numberOfEnclosedTiles = getNumberOfEnclosedTiles(map)

    println("Task 2: Number of enclosed tiles: $numberOfEnclosedTiles")


    println("----------------------------------------")
}

fun getNumberOfEnclosedTiles(map: List<List<Tile>>): Any {
    val loopCoordinates = findLoop(map, findStart(map))
    val area = map
        .mapIndexed { y, row ->
            List(row.size) { x ->
                when {
                    x to y in loopCoordinates -> TileCategory.LOOP_BORDER
                    !map.getTile(x to y).isPipe -> TileCategory.EMPTY
                    else -> TileCategory.UNKNOWN
                }
            }
        }

    val mutableArea = area.map { it.toMutableList() }.toMutableList()

    mutableArea.forEach { row ->
        row.forEach(::println)
    }

    // Starte floodFill von einem Punkt außerhalb des Loops, z.B. (0,0)
    floodFill(mutableArea, 0 to 0, TileCategory.EMPTY, TileCategory.OUTSIDE_LOOP)

    mutableArea.forEach { row ->
        row.forEach(::println)
    }

    return mutableArea.sumOf { row -> row.count { it == TileCategory.UNKNOWN } }
}

fun floodFill(
    area: MutableList<MutableList<TileCategory>>,
    startPosition: Pair<Int, Int>,
    targetCategory: TileCategory,
    replacementCategory: TileCategory
) {
    val (x, y) = startPosition

    // Prüfe, ob die Koordinaten innerhalb der Grenzen liegen und nicht Teil des Loops sind
    if (x < 0 || x >= area[0].size || y < 0 || y >= area.size || area[y][x] != targetCategory) return

    // Markiere das aktuelle Tile als außerhalb des Loops
    area[y][x] = replacementCategory

    // Wende den Algorithmus rekursiv auf die benachbarten Tiles an
    floodFill(area, Pair(x + 1, y), targetCategory, replacementCategory)
    floodFill(area, Pair(x - 1, y), targetCategory, replacementCategory)
    floodFill(area, Pair(x, y + 1), targetCategory, replacementCategory)
    floodFill(area, Pair(x, y - 1), targetCategory, replacementCategory)
}


fun getLoopLength(map: List<List<Tile>>): Int {
    val startCoordinates = findStart(map)

    return findLoop(map, startCoordinates).size
}

private fun findStart(map: List<List<Tile>>): Pair<Int, Int> {
    var startCoordinates: Pair<Int, Int>? = null

    for (y in map.indices) {
        for (x in map[y].indices) {
            if (map[y][x].isStart) {
                startCoordinates = x to y
            }
        }
    }

    if (startCoordinates == null) {
        throw Exception("No start tile found")
    }
    return startCoordinates
}

fun findLoop(map: List<List<Tile>>, startCoordinates: Pair<Int, Int>): List<Pair<Int, Int>> {
    val potentialLoopStarts = mutableListOf<Pair<Int, Int>>()
    val (x, y) = startCoordinates

    if (x - 1 >= 0 && isConnectedPipe(map, x to y, x - 1 to y)) {
        potentialLoopStarts.add(x - 1 to y)
    }
    if (x + 1 < map[y].size && isConnectedPipe(map, x to y, x + 1 to y)) {
        potentialLoopStarts.add(x + 1 to y)
    }
    if (y - 1 >= 0 && isConnectedPipe(map, x to y, x to y - 1)) {
        potentialLoopStarts.add(x to y - 1)
    }
    if (y + 1 < map.size && isConnectedPipe(map, x to y, x to y + 1)) {
        potentialLoopStarts.add(x to y + 1)
    }

    if (potentialLoopStarts.isEmpty()) {
        throw Exception("No loop found")
    }

    for (potentialLoopStart in potentialLoopStarts) {
        val loop = evaluatePath(map, potentialLoopStart, startCoordinates)
        if (loop.isNotEmpty()) {
            return loop
        }
    }

    throw Exception("No loop found")
}

fun isConnectedPipe(map: List<List<Tile>>, current: Pair<Int, Int>, target: Pair<Int, Int>): Boolean {
    return if (!map.getTile(target).isPipe) {
        false
    } else if (map.getTile(target).isStart) {
        true
    } else {
        arePipesConnected(map, current, target)
    }
}

/**
 * returns empty list if no loop found
 */
fun evaluatePath(
    map: List<List<Tile>>,
    potentialLoopStart: Pair<Int, Int>,
    startCoordinates: Pair<Int, Int>
): List<Pair<Int, Int>> {
    var currentCoordinates = potentialLoopStart
    val loopPath = mutableListOf<Pair<Int, Int>>()
    loopPath.add(startCoordinates)

    while (currentCoordinates != startCoordinates) {
        loopPath.add(currentCoordinates)
        val (x, y) = currentCoordinates
        val (d1, d2) = map[y][x].getNextTilePositionDeltas()

        val connectedPipes = listOf(d1, d2)
            .map { currentCoordinates.applyDirection(it) }
            .filter { isConnectedPipe(map, currentCoordinates, it) }

        if (connectedPipes.size <= 1) {
            return listOf()
        } else {
            currentCoordinates = getNextCoordinate(connectedPipes, map, loopPath)
        }
    }

    return loopPath
}

private fun getNextCoordinate(
    connectedPipes: List<Pair<Int, Int>>,
    map: List<List<Tile>>,
    loopPath: List<Pair<Int, Int>>
): Pair<Int, Int> {
    require(connectedPipes.size == 2)
    return when {
        map.getTile(connectedPipes[0]).isStart -> when {
            connectedPipes[1] in loopPath -> connectedPipes[0]
            else -> connectedPipes[1]
        }

        map.getTile(connectedPipes[1]).isStart -> when {
            connectedPipes[0] in loopPath -> connectedPipes[1]
            else -> connectedPipes[0]
        }

        connectedPipes[0] in loopPath -> connectedPipes[1]
        connectedPipes[1] in loopPath -> connectedPipes[0]
        else -> throw Exception("Unexpected path")
    }
}

fun arePipesConnected(map: List<List<Tile>>, current: Pair<Int, Int>, target: Pair<Int, Int>): Boolean {
    val targetDeltas = map.getTile(target).getNextTilePositionDeltas()

    return listOf(targetDeltas.first, targetDeltas.second)
        .map { target.applyDirection(it) }
        .any { it == current }
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

fun List<List<Tile>>.getTile(position: Pair<Int, Int>): Tile {
    val (x, y) = position
    return this[y][x]
}

fun Pair<Int, Int>.applyDirection(direction: Direction): Pair<Int, Int> {
    val (x, y) = direction.toDelta()
    return this.first + x to this.second + y
}

/*
    | is a vertical pipe connecting north and south.
    - is a horizontal pipe connecting east and west.
    L is a 90-degree bend connecting north and east.
    J is a 90-degree bend connecting north and west.
    7 is a 90-degree bend connecting south and west.
    F is a 90-degree bend connecting south and east.
    . is ground; there is no pipe in this tile.
    S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.

 */
data class Tile(val pipeInfo: Char) {

    val isStart: Boolean = pipeInfo == 'S'
    val isPipe: Boolean = pipeInfo != '.'

    fun getNextTilePositionDeltas(): Pair<Direction, Direction> {
        return when (pipeInfo) {
            '|' -> NORTH to SOUTH
            '-' -> EAST to WEST
            'L' -> NORTH to EAST
            'J' -> NORTH to WEST
            '7' -> SOUTH to WEST
            'F' -> SOUTH to EAST
            else -> NONE to NONE
        }
    }
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST, NONE;

    fun toDelta() = when (this) {
        NORTH -> 0 to -1
        EAST -> 1 to 0
        SOUTH -> 0 to 1
        WEST -> -1 to 0
        NONE -> 0 to 0
    }
}

enum class TileCategory {
    EMPTY, LOOP_BORDER, OUTSIDE_LOOP, UNKNOWN;

    override fun toString(): String {
        return when (this) {
            EMPTY -> "*"
            LOOP_BORDER -> "X"
            OUTSIDE_LOOP -> "O"
            UNKNOWN -> "?"
        }
    }
}
