import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day  -----")

    println("----------------------------------------")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

