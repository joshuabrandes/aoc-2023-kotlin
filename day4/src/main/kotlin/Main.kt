import java.io.File
import java.util.*

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
        val copies = MutableList(cards.size) { 1 }
        val queue : Queue<Int> = LinkedList()

    // init queue with original indexes
    cards.forEachIndexed { index, _ -> queue.add(index) }

    while (queue.isNotEmpty()) {
        val cardIndex = queue.remove()
        val matches = cards[cardIndex].winningNumberCount

        for (i in 1..matches) {
            val copyIndex = cardIndex + i
            if (copyIndex < cards.size) {
                queue.add(copyIndex)
                copies[copyIndex] += copies[cardIndex]
            }
        }
    }

    return copies.map { it.toLong() }
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("day4-input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class Card(
    val number: Int,
    val winningNumbers: List<Int>,
    val chosenNumbers: List<Int>,
    var isProcessed: Boolean = false
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