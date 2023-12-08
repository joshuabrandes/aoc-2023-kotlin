import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainKtTest {

    /* Data:
    LR

    11A = (11B, XXX)
    11B = (XXX, 11Z)
    11Z = (11B, XXX)
    22A = (22B, XXX)
    22B = (22C, 22C)
    22C = (22Z, 22Z)
    22Z = (22B, 22B)
    XXX = (XXX, XXX)
     */
    @Test
    fun shortestPathForGhostsTest() {
        val result = shortestPathForGhosts(
            "A",
            "Z",
            listOf('L', 'R'),
            listOf(
                Node("11A", "11B", "XXX"),
                Node("11B", "XXX", "11Z"),
                Node("11Z", "11B", "XXX"),
                Node("22A", "22B", "XXX"),
                Node("22B", "22C", "22C"),
                Node("22C", "22Z", "22Z"),
                Node("22Z", "22B", "22B"),
                Node("XXX", "XXX", "XXX")
            )
        )

        assertEquals(6, result)
    }
}
