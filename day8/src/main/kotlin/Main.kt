import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 8 -----")

    val puzzleInput = getPuzzleInput()
    val (instructions, nodes) = getInstructionsAndNodes(puzzleInput)
    val stepsFromAAAToZZZ = stepsFromTo("AAA", "ZZZ", instructions, nodes)

    println("Part 1: Steps from AAA to ZZZ: $stepsFromAAAToZZZ")

    val shortestPathForGhosts = shortestPathForGhosts("A", "Z", instructions, nodes)

    println("Part 2: Shortest path for ghosts: $shortestPathForGhosts")

    println("----------------------------------------")
}

fun shortestPathForGhosts(
    start: String,
    end: String,
    instructions: List<Char>,
    nodes: List<Node>,
    withLogging: Boolean = false
): Any {
    val startNodes = nodes.filter { it.name.endsWith(start) }.map { it.name }.toSet()
    val endNodes = nodes.filter { it.name.endsWith(end) }.map { it.name }.toSet()
    val nodeMap = nodes.associateBy { it.name }

    var currentNodes = startNodes
    var steps = 0

    while (true) {
        for (instruction in instructions) {
            if (withLogging) println("Step $steps: $startNodes")
            if (currentNodes.all { it in endNodes }) {
                println("Reached $currentNodes after $steps steps")
                return steps
            }

            currentNodes = currentNodes
                .map { nodeMap[it]!! }
                .map {
                    when (instruction) {
                        'L' -> nodeMap[it.left]!!
                        'R' -> nodeMap[it.right]!!
                        else -> error("Unknown instruction: $instruction")
                    }
                }
                .map { it.name }
                .toSet()

            steps++
        }
    }
}

fun stepsFromTo(
    startNode: String,
    endNode: String,
    instructions: List<Char>,
    nodes: List<Node>,
    withLogging: Boolean = false
): Int {
    val nodeMap = nodes.associateBy { it.name }
    var currentNode = nodeMap[startNode]!!
    var steps = 0

    while (true) {
        for (instruction in instructions) {
            if (withLogging) println("Step $steps: $currentNode")

            if (currentNode.name == endNode) {
                return steps
            }
            currentNode = when (instruction) {
                'L' -> nodeMap[currentNode.left]!!
                'R' -> nodeMap[currentNode.right]!!
                else -> error("Unknown instruction: $instruction")
            }
            steps++
        }
    }
}

fun getInstructionsAndNodes(puzzleInput: List<String>): Pair<List<Char>, List<Node>> {
    val instructions = puzzleInput.first().trim().toCharArray().toList()
    // example: VTM = (VPB, NKT)
    val nodes = puzzleInput.drop(2)
        .map { it.split(" = ", limit = 2) }
        .map { Node(it[0], it[1].substring(1, 4), it[1].substring(6, 9)) }

    return instructions to nodes
}

fun getPuzzleInput(): List<String> {
    val fileUrl = ClassLoader.getSystemResource("input.txt")
    return File(fileUrl.toURI()).readLines()
}

data class Node(val name: String, val left: String, val right: String) {
    override fun toString(): String {
        return "$name: ($left, $right)"
    }
}
