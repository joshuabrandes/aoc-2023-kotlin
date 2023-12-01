import java.io.File

fun main(args: Array<String>) {
    println("------ Advent of Code 2023 - Day 1 -----")
    val result: Int = getPuzzleInput()
        .sumOf { getCalibrationValue(it) }

    println("Result: $result")
    println("----------------------------------------")
}

fun getCalibrationValue(text: String): Int {
    val firstDigit = getFirstDigit(text)
    val lastDigit = getLastDigit(text)

    return firstDigit * 10 + lastDigit
}

fun getFirstDigit(text: String): Int {
    for (i in text.indices) {
        if (text[i].isDigit()) {
            // println("Calibration value: ${text[i]}")
            return text[i].digitToInt()
        }
    }
    throw Exception("No digit found")
}

fun getLastDigit(text: String): Int {
    for (i in text.length - 1 downTo 0) {
        if (text[i].isDigit()) {
            // println("Calibration value: ${text[i]}")
            return text[i].digitToInt()
        }
    }
    throw Exception("No digit found")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("puzzle-input.txt")
    return File(fileUrl.toURI()).readLines()
}