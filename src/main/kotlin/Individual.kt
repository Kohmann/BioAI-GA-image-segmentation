
/**
 * Class representing an individual in the population.
 * Contains a chromosome and functions for calculating fitness, crossover and mutation.
 */
enum class Direction {
    LEFT, RIGHT, UP, DOWN, NONE
}

class Individual(private val image: ImageObject) {
    // Image information
    private val imgWidth = image.getWidth()
    private val imgHeight = image.getHeight()
    private val geneSize = imgHeight * imgWidth

    val chromosome: Array<Direction> = construction()
    private val segments: ArrayList<MutableList<Int>> = ArrayList()

    // Objective functions
    val overallDeviation: Double = 0.0
    val connectivity: Double = 0.0
    val edgeValue: Double = 0.0

    val rank: Int = 0


    private fun construction(): Array<Direction> {
        // TODO: Implement heuristic construction of the chromosome. Now it's just random.
        // Like Minimal Spanding Tree, use Prims algorithm
        val possibleDirections = listOf<Direction>(Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP)
        val randChromosome = Array(geneSize) { possibleDirections.random() }
        // test chromosome
        /*val test = Array(geneSize) { Direction.NONE }
        test[0] = Direction.RIGHT
        test[1] = Direction.RIGHT
        test[2] = Direction.LEFT
        test[3] = Direction.UP
        test[4] = Direction.LEFT
        test[5] = Direction.UP
        test[6] = Direction.RIGHT
        test[7] = Direction.RIGHT
        test[8] = Direction.LEFT
         */

        return correctChromosome(randChromosome)
    }
    private fun correctChromosome(chromosome: Array<Direction>): Array<Direction> {
        /**
         * Corrects the chromosome by replacing illegal directions with a legal one.
         * Currently inefficient, but works. Should only check the borders of the image.
         */
        for (i in chromosome.indices) {
            val legalDirections = getLegalDirections(i)
            if (chromosome[i] !in legalDirections) {
                chromosome[i] = Direction.NONE //legalDirections.random()
            }
        }
        return chromosome
    }
    private fun getLegalDirections(currentPosition: Int): Array<Direction> {
        val legalMoves = ArrayList<Direction>()
        if (currentPosition % this.imgWidth != 0) {
            legalMoves.add(Direction.LEFT)
        }
        if (currentPosition % this.imgWidth != this.imgWidth - 1) {
            legalMoves.add(Direction.RIGHT)
        }
        if (currentPosition / this.imgWidth != 0) {
            legalMoves.add(Direction.UP)
        }
        if (currentPosition / this.imgWidth != this.imgHeight - 1) {
            legalMoves.add(Direction.DOWN)
        }
        legalMoves.add(Direction.NONE)
        return legalMoves.toTypedArray()
    }

    fun createSegments() {
        /**
         * Creates the segments of the image by traversing the graph connecting the pixels
         * Does not pay attention to the direction of the edges.
         */
        val unvisitedNodes = MutableList(geneSize) { it }

        while (unvisitedNodes.isNotEmpty()) {
            val node = unvisitedNodes.removeFirst()

            val segment = getConnectedNodes(node)
            if (segment.isEmpty())
                this.segments.add(mutableListOf(node))
            else
                this.segments.add(segment)

            unvisitedNodes.removeAll(segment)
        }
        // this.segments.forEach { println(it) }
    }
    private fun getConnectedNodes(i: Int, visited: MutableList<Int> = mutableListOf()): MutableList<Int> {
        /**
         * Returns the connected nodes of the given start node.
         */
        val connections = ArrayList<Int>()

        if (chromosome[i] == Direction.NONE) {
            return connections
        }

        // If the node on the left points RIGHT
        if ((i - 1) >= 0) {
            if (chromosome[i-1] == Direction.RIGHT && i-1 !in visited)  // not visited before
                connections.add(i-1)
        }
        // If the node on the right points LEFT
        if ((i + 1) < this.geneSize ) {
            if (chromosome[i + 1] == Direction.LEFT && i+1 !in visited) {
                connections.add(i + 1)
            }
        }
        // If the node on the top points DOWN
        if ((i - this.imgWidth) / this.imgWidth != 0 && (i - this.imgWidth) >= 0) {
            if (chromosome[i - this.imgWidth] == Direction.DOWN && i-this.imgWidth !in visited) {
                connections.add(i - this.imgWidth)
            }
        }
        // If the node on the bottom points UP
        if ((i + this.imgWidth) < this.geneSize) {
            if (chromosome[i + this.imgWidth] == Direction.UP && i+this.imgWidth !in visited) {
                connections.add(i + this.imgWidth)
            }
        }


        // The node it points to
        val nextNode = getNextNode(i)

        // If it is visited, dont add, else add
        if (nextNode in visited) {
            connections.remove(nextNode)
        }
        else { // only add of not already in the list
            if (nextNode !in connections)
                connections.add(nextNode)
        }

        // adds all new connections to visited list
        for (node in connections) {
            if (node !in visited) {
                visited.add(node)
            }
        }

        //print("$i, connections: ${connections.toList()}")
        //println(", visited nodes: ${visited.toList()}")

        // stores the new connections made in this call
        val newConnections = connections.toMutableList()

        // Loops through all the connections and continue to find more connections, recursively
        for (node in connections) {
            //println("\tCalled with $node, visited: ${visited.toList()}")

            val childConnections = getConnectedNodes(node, visited)
            newConnections.addAll(childConnections)

            // Add new connections to visited list
            for (node in newConnections) {
                if (node !in visited) {
                    visited.add(node)
                }
            }
            //println("\t\t$i, childConnections returned from $node: ${childConnections.toList()}. all connections: ${newConnections.toList()}")
        }
        //println("\tFinal: $i, connections: ${newConnections.toList()}")
        return newConnections
    }
    private fun getNextNode(i: Int): Int {
        // Return the index to the node it points to
        return when (chromosome[i]) {
            Direction.LEFT -> i - 1
            Direction.RIGHT -> i + 1
            Direction.UP -> i - this.imgWidth
            Direction.DOWN -> i + this.imgWidth
            else -> i
        }
    }


    fun crossover(parentB: Individual): Individual {
        TODO("Not yet implemented")
    }

    fun mutate(mutationRate: Double) {
        TODO("Not yet implemented")
    }

    fun calculateFitnesses() {
        // Calculate the fitness for each objective
        TODO("Not yet implemented")
    }

}
