package org.example

import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 9 -----")

    val puzzleInput = getPuzzleInput()

    println("----------------------------------------")

}

fun getPuzzleInput(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").toURI())
        .readLines()
}

data class Signal(val intensity: SignalIntensity, val senderType: SenderType, val sender: String, val receiver: String)

enum class SignalIntensity {
    LOW, HIGH
}

enum class SenderType {
    FLIP_FLOP, INVERTER
}
