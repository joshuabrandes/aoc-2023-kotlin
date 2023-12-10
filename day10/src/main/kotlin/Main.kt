package org.example

import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 10 -----")

    val puzzleInput = getPuzzleInput()



    println("----------------------------------------")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}
