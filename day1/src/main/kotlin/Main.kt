import java.io.File

val startChars = listOf("o", "t", "f", "s", "e", "n")

fun main() {
    println("------ Advent of Code 2023 - Day 1 -----")
    val puzzleInput = getPuzzleInput()
    val resultTask1 = puzzleInput.sumOf { getCalibrationValue(it) }
    val resultTask2 = puzzleInput.sumOf { getCalibrationValue(it, true) }

    println("Result Task 1: $resultTask1")
    println("Result Task 2: $resultTask2")
    println("----------------------------------------")
}

fun getCalibrationValue(text: String, includeWrittenNumbers: Boolean = false): Int {
    val firstDigit = getFirstDigit(text, includeWrittenNumbers)
    val lastDigit = getLastDigit(text, includeWrittenNumbers)

    return firstDigit * 10 + lastDigit
}

fun getFirstDigit(text: String, includeWrittenNumbers: Boolean = false): Int {
    for (i in text.indices) {
        if (includeWrittenNumbers && startChars.contains(text[i].toString())) {
            if (matchStartChar(text, i) != null) {
                return matchStartChar(text, i)!!
            }
        } else
        if (text[i].isDigit()) {
            return text[i].digitToInt()
        }
    }
    throw Exception("No digit found")
}

fun getLastDigit(text: String, includeWrittenNumbers: Boolean = false): Int {
    for (i in text.length - 1 downTo 0) {
        if (includeWrittenNumbers && startChars.contains(text[i].toString())) {
            if (matchStartChar(text, i) != null) {
                return matchStartChar(text, i)!!
            }
        } else if (text[i].isDigit()) {
            return text[i].digitToInt()
        }
    }
    throw Exception("No digit found")
}

fun containsNumber(text: String, i: Int, s: String): Boolean {
    val substringMaxFive =
        if (i + 5 > text.length) text.substring(i)
        else text.substring(i, i + 5)
    return substringMaxFive.contains(s)
}

fun matchStartChar(text: String, i: Int): Int? {
    return when {
        (text[i] == 'o') && containsNumber(text, i, "one") -> 1
        (text[i] == 't') && containsNumber(text, i, "two") -> 2
        (text[i] == 't') && containsNumber(text, i, "three") -> 3
        (text[i] == 'f') && containsNumber(text, i, "four") -> 4
        (text[i] == 'f') && containsNumber(text, i, "five") -> 5
        (text[i] == 's') && containsNumber(text, i, "six") -> 6
        (text[i] == 's') && containsNumber(text, i, "seven") -> 7
        (text[i] == 'e') && containsNumber(text, i, "eight") -> 8
        (text[i] == 'n') && containsNumber(text, i, "nine") -> 9
        else -> null
    }
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("puzzle-input.txt")
    return File(fileUrl.toURI()).readLines()
}