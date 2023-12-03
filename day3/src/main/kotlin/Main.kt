import java.io.File

val symbols = setOf('*', '#', '+', '$')

fun main(args: Array<String>) {
    println("------ Advent of Code 2023 - Day 2 -----")

    val engineSchematic = if (args.isEmpty()) getPuzzleInput() else args.toList()
    val validNumbers = getValidNumbers(engineSchematic)


    println("Task 1: Sum of all valid numbers: ${validNumbers.sumOf { it.first }}")


    println("----------------------------------------")
}

fun getValidNumbers(engineSchematic: List<String>): MutableList<Pair<Int, Pair<Int, Int>>> {
    val numbersWithPositions = mutableListOf<Pair<Int, Pair<Int, Int>>>()

    // 1. Find all numbers and their positions
    for (y in engineSchematic.indices) {
        var x = 0
        while (x < engineSchematic[y].length) {
            if (engineSchematic[y][x].isDigit()) {
                val result = completeNumberAndGetLastIndex(engineSchematic, x, y)
                val number = result.first
                numbersWithPositions.add(number to (x to y))

                // Skip to the end of the number
                x = result.second
            }
            x++
        }
    }

    // 2. Find valid numbers (numbers with at least 1 symbol around)
    val validNumbers = mutableListOf<Pair<Int, Pair<Int, Int>>>()
    for ((number, position) in numbersWithPositions) {
        val (x, y) = position
        if (isValidNumber(engineSchematic, x, y))
            validNumbers.add(number to position)
    }
    return validNumbers
}

fun completeNumberAndGetLastIndex(schematic: List<String>, x: Int, y: Int): Pair<Int, Int> {
    var x1 = x
    var number = schematic[y][x].toString()

    while (x1 + 1 < schematic[y].length) {
        if (schematic[y][x1 + 1].isDigit()) {
            number += schematic[y][x1 + 1]
            x1++
        } else break
    }

    return number.toInt() to x1
}

fun isValidNumber(schematic: List<String>, x: Int, y: Int): Boolean {
    val xFirst = x - 1
    val xLast = getNumberLastPosition(schematic[y], x) + 1

    // Check if there is a symbol before the number
    if ((xFirst >= 0) && isSymbol(schematic[y][xFirst])) {
        return true
    }
    // Check if there is a symbol after the number
    if ((xLast < schematic[y].length) && isSymbol(schematic[y][xLast])) {
        return true
    }
    // Check if there is a symbol above the number
    if ((y > 0) && areaContainsSymbol(schematic[y - 1], xFirst, xLast)) {
        return true
    }
    // Check if there is a symbol below the number
    if ((y + 1 < schematic.size) && areaContainsSymbol(schematic[y + 1], xFirst, xLast)) {
        return true
    }

    return false
}

fun getNumberLastPosition(line: String, x: Int): Int {
    var lastPosition = x

    while (lastPosition + 1 < line.length) {
        if (line[lastPosition + 1].isDigit()) {
            lastPosition++
        } else break
    }

    return lastPosition
}

fun areaContainsSymbol(line: String, xFirst: Int, xLast: Int): Boolean {
    for (x in xFirst..xLast) {
        if ((x in line.indices) && isSymbol(line[x])) {
            return true
        }
    }

    return false
}

fun isSymbol(symbol: Char): Boolean {
    return symbols.contains(symbol)
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("day3-input.txt")
    return File(fileUrl.toURI()).readLines()
}