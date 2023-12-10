import org.example.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainKtTest {

    private fun getTileMapAndReplaceWith(map: List<String>, replacement: String): List<List<Tile>> {
        return map
            .map { it.replace("*", replacement) }
            .map { it.toCharArray().map { Tile(it) } }
    }

    private fun getCharsFromDeltas(map: List<List<Tile>>) : List<Char> {
        val center = 1 to 1
        val (d1, d2) = map.getTile(center).getNextTilePositionDeltas()
        return listOf(d1, d2)
            .asSequence()
            .map { center.applyDirection(it) }
            .map { map.getTile(it).pipeInfo }
            .map { it.digitToInt() }
            .sorted()
            .map { it.toString().first() }
            .toList()
    }

    @Test
    fun `correct provided deltas`() {
        val map = listOf(
            "123",
            "4*5",
            "678"
        )

        val mapWithI = getTileMapAndReplaceWith(map, "|")
        val mapWithDash = getTileMapAndReplaceWith(map, "-")
        val mapWithL = getTileMapAndReplaceWith(map, "L")
        val mapWithJ = getTileMapAndReplaceWith(map, "J")
        val mapWith7 = getTileMapAndReplaceWith(map, "7")
        val mapWithF = getTileMapAndReplaceWith(map, "F")

        assertEquals(listOf('2', '7'), getCharsFromDeltas(mapWithI))
        assertEquals(listOf('4', '5'), getCharsFromDeltas(mapWithDash))
        assertEquals(listOf('2', '5'), getCharsFromDeltas(mapWithL))
        assertEquals(listOf('2', '4'), getCharsFromDeltas(mapWithJ))
        assertEquals(listOf('4', '7'), getCharsFromDeltas(mapWith7))
        assertEquals(listOf('5', '7'), getCharsFromDeltas(mapWithF))
    }

    @Test
    fun `getLoopLength should return 16`() {
        val map = getExamplePart1()
            .map { it.toCharArray().map { Tile(it) } }

        val loopLength = getLoopLength(map)

        assertEquals(16, loopLength)
    }

    @Test
    fun `enclosed area should return 4 for example 2_1`() {
        val map = getExamplePart2_1()
            .map { it.toCharArray().map { Tile(it) } }

        val enclosedArea = getNumberOfEnclosedTiles(map)

        assertEquals(4, enclosedArea)
    }

    @Test
    fun `enclosed area should return 4 for example 2_2`() {
        val map = getExamplePart2_2()
            .map { it.toCharArray().map { Tile(it) } }

        val enclosedArea = getNumberOfEnclosedTiles(map)

        assertEquals(4, enclosedArea)
    }

    private fun getExamplePart1(): List<String> {
        return listOf(
            "..F7.",
            ".FJ|.",
            "SJ.L7",
            "|F--J",
            "LJ..."
        )
    }

    private fun getExamplePart2_1(): List<String> {
        return listOf(
            "...........",
            ".S-------7.",
            ".|F-----7|.",
            ".||.....||.",
            ".||.....||.",
            ".|L-7.F-J|.",
            ".|..|.|..|.",
            ".L--J.L--J.",
            "..........."
        )
    }

    private fun getExamplePart2_2(): List<String> {
        return listOf(
            "..........",
            ".S------7.",
            ".|F----7|.",
            ".||....||.",
            ".||....||.",
            ".|L-7F-J|.",
            ".|..||..|.",
            ".L--JL--J.",
            ".........."
        )
    }
}