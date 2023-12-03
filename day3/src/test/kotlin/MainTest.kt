import kotlin.test.Test

class MainTest {
    @Test
    fun test() {
        val result = getValidNumbers(
            listOf(
                "467..114..",
                "...*......",
                "..35..633.",
                "......#...",
                "617*......",
                ".....+.58.",
                "..592.....",
                "......755.",
                "...\$.*....",
                ".664.598.."
            )
        )

        val numbers = result.map { it.first }
        assert(!numbers.contains(114))
        assert(!numbers.contains(58))

        assert(result.sumOf { it.first } == 4361)
    }
}