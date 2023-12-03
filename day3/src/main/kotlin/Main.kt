import java.io.File

fun main(args: Array<String>) {
    println("------ Advent of Code 2023 - Day 3 -----")

    val engineSchematic = if (args.isEmpty()) getPuzzleInput() else args.toList()
    val validNumbers = getValidNumbers(engineSchematic)


    println("Task 1: Sum of all valid numbers: ${validNumbers.sumOf { it.first }}")
    println("Task 2: Sum of all gear ratios: ${getSumOfAllGearRatios(engineSchematic)}")

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

fun getSumOfAllGearRatios(schematic: List<String>): Int {
    val gearPositions = mutableListOf<Pair<Int, Int>>()

    // 1. Find all potential gear positions
    for (y in schematic.indices) {
        for (x in schematic[y].indices) {
            if (schematic[y][x] == '*') {
                gearPositions.add(x to y)
            }
        }
    }

    // 2. Find all actual gears
    val gears = mutableListOf<Gear>()
    for ((x, y) in gearPositions) {
        val adjacentGears = findAdjacentNumbers(schematic, x, y)
        if (adjacentGears.size == 2)
            gears.add(Gear(adjacentGears[0], adjacentGears[1]))
    }

    // 3. Calculate the sum of all gear ratios
    return gears.sumOf { it.ratio }
}

fun findAdjacentNumbers(schematic: List<String>, x: Int, y: Int): List<Int> {
    val adjacentNumbers = mutableListOf<Int>()
    val xLeft = if (x - 1 >= 0) x - 1 else x
    val xRight = if (x + 1 < schematic[y].length) x + 1 else x

    // find potential left number
    if (schematic[y][xLeft].isDigit()) {
        adjacentNumbers.add(getFullNumber(schematic, xLeft, y))
    }

    // find potential right number
    if (schematic[y][xRight].isDigit()) {
        adjacentNumbers.add(getFullNumber(schematic, xRight, y))
    }

    // find potential top numbers
    val topNumbers = findNumbersInRow(schematic, xLeft, xRight, y + 1)
    adjacentNumbers.addAll(topNumbers)

    // find potential bottom numbers
    val bottomNumbers = findNumbersInRow(schematic, xLeft, xRight, y - 1)
    adjacentNumbers.addAll(bottomNumbers)

    return adjacentNumbers
}

fun findNumbersInRow(schematic: List<String>, xLeft: Int, xRight: Int, y: Int): List<Int> {
    val numbers = mutableListOf<Int>()

    // check if the row exists
    if (y !in schematic.indices) {
        return numbers
    }

    // check if potential middle number exists
    if ((xRight - xLeft > 1) && schematic[y][xLeft + 1].isDigit()) {
        numbers.add(getMiddleNumber(schematic, y, xLeft, xRight))
    } else {
        // check if potential left number exists
        if (schematic[y][xLeft].isDigit()) {
            numbers.add(getFullNumber(schematic, xLeft, y))
        }
        // check if potential right number exists
        if (schematic[y][xRight].isDigit()) {
            numbers.add(getFullNumber(schematic, xRight, y))
        }
    }

    return numbers
}

private fun getMiddleNumber(schematic: List<String>, y: Int, xLeft: Int, xRight: Int, ) : Int {
    var middleNumber = schematic[y][xLeft + 1].toString()
    var dX = xRight

    while (dX < schematic[y].length) {
        if (schematic[y][dX].isDigit()) {
            middleNumber += schematic[y][dX]
            dX++
        } else break
    }

    dX = xLeft
    while (dX >= 0) {
        if (schematic[y][dX].isDigit()) {
            middleNumber = schematic[y][dX] + middleNumber
            dX--
        } else break
    }

    return middleNumber.toInt()
}

fun getFullNumber(schematic: List<String>, x: Int, y: Int): Int {
    var number = schematic[y][x].toString()

    var dX = x
    while (dX + 1 < schematic[y].length) {
        if (schematic[y][dX + 1].isDigit()) {
            number += schematic[y][dX + 1]
            dX++
        } else break
    }

    dX = x
    while (dX - 1 >= 0) {
        if (schematic[y][dX - 1].isDigit()) {
            number = schematic[y][dX - 1] + number
            dX--
        } else break
    }

    return number.toInt()
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
    if ((xFirst >= 0) && isValidSymbol(schematic[y][xFirst])) {
        return true
    }
    // Check if there is a symbol after the number
    if ((xLast < schematic[y].length) && isValidSymbol(schematic[y][xLast])) {
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
        if ((x in line.indices) && isValidSymbol(line[x])) {
            return true
        }
    }

    return false
}

fun isValidSymbol(symbol: Char): Boolean {
    return symbol != '.'
}

data class Gear(val number1: Int, val number2: Int) {
    val ratio = number1 * number2
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("day3-input.txt")
    return File(fileUrl.toURI()).readLines()
}