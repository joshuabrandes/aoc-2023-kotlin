package org.example

import java.io.File
import java.util.*

import org.example.Facing.*


fun main() {
    println("------ Advent of Code 2023 - Day 10 -----")

    val puzzleInput = getPuzzleInput()
    val puzzleInputAsString = puzzleInput.joinToString("\n")

    println("Part 1: ${part1(puzzleInputAsString)}")
    println("Part 2: ${part2(puzzleInputAsString)}")

    println("----------------------------------------")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

private val UP = Vec2(0, -1)
private val RIGHT = Vec2(1, 0)
private val DOWN = Vec2(0, 1)
private val LEFT = Vec2(-1, 0)

enum class Facing(val dir: Vec2, val left: Vec2, val right: Vec2, val nextPipes: String) {
    NORTH(dir = UP, left = LEFT, right = RIGHT, nextPipes = "|7F"),
    EAST(dir = RIGHT, left = UP, right = DOWN, nextPipes = "-J7"),
    SOUTH(dir = DOWN, left = RIGHT, right = LEFT, nextPipes = "|LJ"),
    WEST(dir = LEFT, left = DOWN, right = UP, nextPipes = "-LF"),
}

private fun Char.nextFacing(facing: Facing) = when (this) {
    '|' -> when (facing) {
        NORTH, SOUTH -> facing
        EAST, WEST -> illegalInput(facing)
    }
    '-' -> when (facing) {
        EAST, WEST -> facing
        NORTH, SOUTH -> illegalInput(facing)
    }
    'L' -> when (facing) {
        SOUTH -> EAST
        WEST -> NORTH
        NORTH, EAST -> illegalInput(facing)
    }
    'J' -> when (facing) {
        EAST -> NORTH
        SOUTH -> WEST
        NORTH, WEST -> illegalInput(facing)
    }
    '7' -> when (facing) {
        NORTH -> WEST
        EAST -> SOUTH
        SOUTH, WEST -> illegalInput(facing)
    }
    'F' -> when (facing) {
        NORTH -> EAST
        WEST -> SOUTH
        EAST, SOUTH -> illegalInput(facing)
    }
    else -> illegalInput(this)
}

fun illegalInput(facing: Any): Facing {
    error("Illegal input for facing $facing")
}

private fun findLoop(input: String): Map<Vec2, Set<Facing>> {
    val grid = input.lines()
    val s = run {
        val sRow = grid.indexOfFirst { 'S' in it }
        Vec2(x = grid[sRow].indexOf('S'), y = sRow)
    }
    outer@ for (f in Facing.entries) {
        val loop = HashMap<Vec2, Set<Facing>>()
        var facing = f
        var pos = s + f.dir
        while (true) {
            when (val pipe = grid.getOrNull(pos.y)?.getOrNull(pos.x)) {
                null -> continue@outer // out of grid, no loop
                'S' -> {
                    loop[pos] = setOf(f, facing)
                    return loop
                }
                in facing.nextPipes -> {
                    val before = facing
                    facing = pipe.nextFacing(facing)
                    loop[pos] = setOf(before, facing)
                    pos += facing.dir
                }
                else -> continue@outer // pipe doesn't connect with previous, no loop
            }
        }
    }
    error("Did not find loop")
}

fun part1(input: String) = findLoop(input).size / 2

fun part2(input: String): Int {
    val leftOfLoop = HashSet<Vec2>()
    val rightOfLoop = HashSet<Vec2>()
    var leftIsInfinite = false
    var rightIsInfinite = false
    val loop = findLoop(input)
    val xs = loop.keys.minOf(Vec2::x)..loop.keys.maxOf(Vec2::x)
    val ys = loop.keys.minOf(Vec2::y)..loop.keys.maxOf(Vec2::y)
    for ((pos, facings) in loop) {
        for (facing in facings) {
            fun addAllNextToPos(left: Boolean) {
                val lr = if (left) facing.left else facing.right
                generateSequence(pos + lr, lr::plus)
                    .map { pos -> Pair(pos, pos.x in xs && pos.y in ys) }
                    .onEach { (_, inBounds) ->
                        if (!inBounds) if (left) leftIsInfinite = true else rightIsInfinite = true
                    }
                    .takeWhile { (pos, inBounds) -> inBounds && pos !in loop.keys }
                    .forEach { (pos, _) -> (if (left) leftOfLoop else rightOfLoop).add(pos) }
            }
            if (!leftIsInfinite) addAllNextToPos(left = true)
            if (!rightIsInfinite) addAllNextToPos(left = false)
        }
    }
    return when {
        leftIsInfinite && rightIsInfinite -> error("Somehow, both sides of the loop enclose infinite tiles.")
        leftIsInfinite -> rightOfLoop.size
        rightIsInfinite -> leftOfLoop.size
        else -> error("Somehow, both sides of the loop enclose a finite number of tiles.")
    }
}

class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
}
