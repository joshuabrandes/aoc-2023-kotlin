package org.example

import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 6 -----")

    val puzzleInput = getPuzzleInput()
    val games = parseGames(puzzleInput, withJokers = false)
    val rankedGames = rankGames(games)

    println("Task 1: Total winnings: ${getTotalWinnings(rankedGames)}")

    val gamesWithJokers = parseGames(puzzleInput, withJokers = true)
    val rankedGamesWithJokers = rankGames(gamesWithJokers)

    println("Task 2: Total winnings with Jokers: ${getTotalWinnings(rankedGamesWithJokers)}")

    println("----------------------------------------")
}

fun parseGames(puzzleInput: List<String>, withJokers: Boolean): List<Game> {
    return puzzleInput
        .map { if (withJokers) it.replace("J", "*") else it }
        .map { Game.fromString(it) }
}

fun rankGames(games: List<Game>): List<GameWithRank> {
    return games
        .sortedBy { it.hand }
        .mapIndexed { index, game -> GameWithRank(game, index + 1) }
}

fun getTotalWinnings(rankedGames: List<GameWithRank>): Long {
    return rankedGames.sumOf(GameWithRank::value)
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class GameWithRank(val game: Game, val rank: Int) {
    val value: Long
        get() = game.bid.toLong() * rank.toLong()
}

data class Game(val hand: Hand, val bid: Int) : Comparable<Game> {
    companion object {
        fun fromString(input: String): Game {
            val split = input.split(" ", limit = 2)
            val hand = Hand.fromString(split[0])
            val bid = split[1].trim().toInt()
            return Game(hand, bid)
        }
    }

    override fun compareTo(other: Game): Int {
        return hand.compareTo(other.hand)
    }
}

data class Hand(val cards: List<CardFace>) : Comparable<Hand> {

    private val handValue: HandValue = HandValue.fromHand(this)

    override fun compareTo(other: Hand) = comparator.compare(this, other)

    companion object {
        private val comparator = compareBy(Hand::handValue)
            .thenComparator { a, b ->
                val index = (0..<5).firstOrNull { a.cards[it] != b.cards[it] } ?: 0
                a.cards[index].compareTo(b.cards[index])
            }

        fun fromString(input: String): Hand {
            val hands = input.split("")
                .filter { it.isNotBlank() }
                .map { CardFace.fromString(it) }
            return Hand(hands)
        }
    }
}

enum class CardFace(val value: String) : Comparable<CardFace> {
    JOKER("*"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("T"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");

    companion object {
        fun fromString(input: String): CardFace {
            for (cardFace in entries) {
                if (cardFace.value == input) {
                    return cardFace
                }
            }
            throw IllegalArgumentException("Invalid card face: $input")
        }
    }
}

enum class HandValue {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    companion object {
        fun fromHand(hand: Hand): HandValue {
            require(hand.cards.size == 5) { "Hand must contain 5 cards" }
            val (jokers, cards) = hand.cards.partition { it == CardFace.JOKER }
            val numberOfJokers = jokers.size
            val stats = cards.groupingBy { it }.eachCount().values.sortedDescending()

            if (numberOfJokers >= 4) {
                return FIVE_OF_A_KIND
            }

            return when (stats.first()) {
                5 -> FIVE_OF_A_KIND
                4 -> if (numberOfJokers == 1) FIVE_OF_A_KIND else FOUR_OF_A_KIND
                3 -> when (numberOfJokers) {
                    0 -> if (2 in stats.drop(1)) FULL_HOUSE else THREE_OF_A_KIND
                    1 -> FOUR_OF_A_KIND
                    2 -> FIVE_OF_A_KIND
                    else -> error("Impossible state")
                }
                2 -> when (numberOfJokers) {
                    0 -> if (2 in stats.drop(1)) TWO_PAIRS else ONE_PAIR
                    1 -> if (2 in stats.drop(1)) FULL_HOUSE else THREE_OF_A_KIND
                    2 -> FOUR_OF_A_KIND
                    3 -> FIVE_OF_A_KIND
                    else -> error("Impossible state")
                }
                1 -> when (numberOfJokers) {
                    0 -> HIGH_CARD
                    1 -> ONE_PAIR
                    2 -> THREE_OF_A_KIND
                    3 -> FOUR_OF_A_KIND
                    else -> error("Impossible state")
                }

                else -> error("Impossible state")
            }
        }
    }
}
