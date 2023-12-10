package org.example

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.Executors

val myDispatcher = Executors.newFixedThreadPool(24).asCoroutineDispatcher()

fun main() {
    println("------ Advent of Code 2023 - Day 9 -----")

    val puzzleInput = getPuzzleInput()
    val sensorReadings = puzzleInput.map { SensorReading.fromString(it) }
    val sumOfExtrapolatedValues = runBlocking {
        sensorReadings
            .map { async(myDispatcher) { it.getExtrapolatedValue() } }
            .sumOf { it.await() }
    }

    println("Part 1: Sum of extrapolated values: $sumOfExtrapolatedValues")

    val sumOfExtrapolatedHistory = runBlocking {
        sensorReadings
            .map { async(myDispatcher) { it.getExtrapolatedValue(calculateFuture = false) } }
            .sumOf { it.await() }
    }

    myDispatcher.close()

    println("Part 2: Sum of extrapolated history: $sumOfExtrapolatedHistory")

    println("----------------------------------------")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class SensorReading(val readings: List<Long>) {

    fun getExtrapolatedValue(values: List<Long> = readings, calculateFuture: Boolean = true): Long =
        if (values.all { it == 0L })
            0L
        else if (calculateFuture)
            values.last() + getExtrapolatedValue(values.zipWithNext { a, b -> b - a })
        else
            values.first() - getExtrapolatedValue(values.zipWithNext { a, b -> b - a }, false)

    companion object {
        fun fromString(input: String): SensorReading {
            return SensorReading(input
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { it.toLong() })
        }
    }
}
