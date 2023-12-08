import java.io.File

fun main() {
    println("------ Advent of Code 2023 - Day 8 -----")

    val puzzleInput = getPuzzleInput()
    val (instructions, nodes) = getInstructionsAndNodes(puzzleInput)
    val stepsFromAAAToZZZ = stepsFromTo("AAA", { it.name == "ZZZ" }, instructions, nodes.associateBy { it.name })

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
): Long {
    val startNodes = nodes.filter { it.name.endsWith(start) }.map { it.name }.toSet()
    val nodeMap = nodes.associateBy { it.name }

    val allSteps = startNodes
        .map { stepsFromTo(it, { node -> node.name.endsWith(end) }, instructions, nodeMap) }
        .lcm()

    return allSteps
}

fun stepsFromTo(
    startNode: String,
    isEndNode: (Node) -> Boolean,
    instructions: List<Char>,
    nodeMap: Map<String, Node>,
    withLogging: Boolean = false
): Long {
    var currentNode = nodeMap[startNode]!!
    var steps = 0L

    while (true) {
        for (instruction in instructions) {
            if (withLogging) println("Step $steps: $currentNode")

            if (isEndNode(currentNode)) {
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

fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

fun List<Long>.lcm(): Long {
    return this.reduce { acc, num -> lcm(acc, num) }
}



data class Node(val name: String, val left: String, val right: String) {
    override fun toString() = "$name: ($left, $right)"
}
