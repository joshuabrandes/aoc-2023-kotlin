import java.io.File
import kotlin.collections.HashMap

fun main() {
    println("------ Advent of Code 2023 - Day 4 -----")

    val puzzleInput = getPuzzleInput()
    val cards = puzzleInput
        .map(Card::fromString)

    println("Task 1: Sum of points for all cards: ${cards.sumOf(Card::points)}")

    val cardsWithNewRules = getCardsWithNewRules(cards)
    println("Task 2: Amount of cards with new rules: ${cardsWithNewRules.sum()}")

    println("----------------------------------------")
}

fun getCardsWithNewRules(cards: List<Card>): List<Long> {
    val copies = HashMap<Int, Int>()

    // Initialisiere die HashMap mit einer Kopie für jede ursprüngliche Karte
    cards.indices.forEach { copies[it] = 1 }

    for (card in cards) {
        val matches = card.winningNumberCount
        if (matches == 0) {
            continue
        } else {
            val index = card.number - 1
            for (i in 1..matches) {
                copies[index + i] = copies.getOrDefault(index + i, 0) + copies[index]!!
            }
        }
    }

    return copies.values.map { it.toLong() }
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("day4-input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class Card(
    val number: Int,
    val winningNumbers: List<Int>,
    val chosenNumbers: List<Int>,
) {

    val winningNumberCount: Int
        get() = winningNumbers.intersect(chosenNumbers.toSet()).count()

    val points: Int
        get() {
            val matches = winningNumberCount
            return if (matches == 0) {
                0
            } else {
                var points = 1
                for (i in 2..matches) {
                    points *= 2
                }
                points
            }
        }

    companion object {
        fun fromString(input: String): Card {
            val (gameName, numbers) = input.split(": ")
            val (winningNumbers, chosenNumbers) = numbers.split(" | ")
            // Number after "Game "
            val gameNumber = gameName.substring(5).trim().toInt()
            val winningNumbersList = getNumbersFromString(winningNumbers)
            val chosenNumbersList = getNumbersFromString(chosenNumbers)

            return Card(gameNumber, winningNumbersList, chosenNumbersList)
        }

        private fun getNumbersFromString(numbers: String): List<Int> {
            return numbers.split(" ")
                .map(String::trim)
                .filter(String::isNotEmpty)
                .map { it.toInt() }
        }
    }
}