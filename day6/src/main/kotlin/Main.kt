import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day  -----")

    val puzzleInput = getPuzzleInput()
    val races = getRaces(puzzleInput)
    val waysToWin = races
        .map { it.numberOfWaysToWin() }
        .reduce { acc, i -> acc * i }

    println("Task 1: Number of ways to win all Races: $waysToWin")

    val actualRace = getRace(puzzleInput)
    val waysToWinActualRace = actualRace.numberOfWaysToWin()

    println("Task 2: Number of Ways to win actual Race: $waysToWinActualRace")

    println("----------------------------------------")
}

fun getRaces(puzzleInput: List<String>): List<Race> {
    val times = puzzleInput.first()
        .substringAfter("Time: ")
        .split(" ")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toLong() }
    val distances = puzzleInput.last()
        .substringAfter("Distance: ")
        .split(" ")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toLong() }

    assert(times.size == distances.size)
    val races = mutableListOf<Race>()

    for (i in times.indices) {
        races.add(Race(times[i], distances[i]))
    }

    return races
}

fun getRace(puzzleInput: List<String>) : Race {
    val time = puzzleInput.first()
        .substringAfter("Time: ")
        .split("")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .reduce { acc, s -> acc + s }
        .toLong()
    val distance = puzzleInput.last()
        .substringAfter("Distance: ")
        .split("")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .reduce { acc, s -> acc + s }
        .toLong()

    return Race(time, distance)
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class Race(val time: Long, val distance: Long) {

    fun numberOfWaysToWin(): Int {
        var ways = 0
        for (i in 1..<time) {
            if (distanceAfter(i) > distance) {
                ways++
            }
        }

        return ways
    }

    private fun distanceAfter(time: Long): Long {
        return time * (this.time - time)
    }
}
