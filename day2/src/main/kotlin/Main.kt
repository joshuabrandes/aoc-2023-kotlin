import java.io.File

const val NUMBER_OF_RED = 12
const val NUMBER_OF_GREEN = 13
const val NUMBER_OF_BLUE = 14

var minNumberOfRed = 0
var minNumberOfGreen = 0
var minNumberOfBlue = 0

fun main() {
    println("------ Advent of Code 2023 - Day 2 -----")

    val gamesFromInput = getPuzzleInput()
        .map { Game.fromString(it) }
    val possibleGames = gamesFromInput
        .filter { it.isGamePossible() }

    println("Task 1: Sum of possible gameIds: ${possibleGames.sumOf { it.gameId }}")

    val minPowerForGames = gamesFromInput
        .map { MinimumColorCount.fromGame(it) }
        .sumOf { it.getPower() }

    println("Task 2: Minimum power for all games: $minPowerForGames")

    println("----------------------------------------")
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("day2-input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class MinimumColorCount(val red: Int, val green: Int, val blue: Int) {

    fun getPower(): Int {
        return red * green * blue
    }

    companion object {
        fun fromGame(game: Game): MinimumColorCount {
            var red = 0
            var green = 0
            var blue = 0

            for (turn in game.turns) {
                val redTurn = turn.turnResult[Color.RED] ?: 0
                val greenTurn = turn.turnResult[Color.GREEN] ?: 0
                val blueTurn = turn.turnResult[Color.BLUE] ?: 0

                if (redTurn > red) {
                    red = redTurn
                }
                if (greenTurn > green) {
                    green = greenTurn
                }
                if (blueTurn > blue) {
                    blue = blueTurn
                }
            }

            return MinimumColorCount(red, green, blue)
        }
    }
}

data class Game(private val _gameId: Int, private val _turns: List<Turn>) {

    val gameId: Int
        get() = _gameId

    val turns: List<Turn>
        get() = _turns

    fun isGamePossible(): Boolean {
        for (turn in _turns) {
            if (!turn.isTurnPossible()) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "Game $_gameId: ${_turns.joinToString("; ")}"
    }

    companion object {
        fun fromString(input: String): Game {
            val infos = input.split(": ")
                .map { it.trim() }
            assert(infos.size == 2) { "Invalid input: $input" }

            // get gameId after "Game "
            val gameId = infos[0].substring(5).toInt()
            val turns = infos[1].split("; ")
                .map { it.trim() }
                .map { Turn.fromString(it) }

            return Game(gameId, turns)
        }
    }
}

data class Turn(private val _turnResult: Map<Color, Int>) {

    val turnResult: Map<Color, Int>
        get() = _turnResult

    override fun toString(): String {
        return _turnResult.map { "${it.value} ${it.key.color}" }.joinToString(", ")
    }

    fun isTurnPossible(): Boolean {
        val red = _turnResult[Color.RED] ?: 0
        val green = _turnResult[Color.GREEN] ?: 0
        val blue = _turnResult[Color.BLUE] ?: 0

        return red <= NUMBER_OF_RED
                && green <= NUMBER_OF_GREEN
                && blue <= NUMBER_OF_BLUE
    }

    companion object {
        fun fromString(inputString: String): Turn {
            val infoMap = inputString.split(", ")
                .map { it.trim() }
                .map {
                    val parts = it.split(" ")
                    assert(parts.size == 2) { "Invalid input: $inputString" }
                    return@map parts
                }
                .associate { Color.fromString(it[1]) to it[0].toInt() }

            return Turn(infoMap)
        }
    }
}

enum class Color(val color: String) {
    RED("red"),
    GREEN("green"),
    BLUE("blue");

    companion object {
        fun fromString(color: String): Color {
            return when (color) {
                "red" -> RED
                "green" -> GREEN
                "blue" -> BLUE
                else -> throw IllegalArgumentException("Invalid color: $color")
            }
        }
    }
}
