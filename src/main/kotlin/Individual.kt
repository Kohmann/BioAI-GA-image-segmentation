import java.awt.Color
import java.awt.image.Kernel

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

    // Objective functions
    var overallDeviation: Double = 0.0
    var connectivity: Double = 0.0
    var edgeValue: Double = 0.0

    val rank: Int = 0

    val chromosome: Array<Direction> = construction()
    val segments: ArrayList<MutableSet<Int>> = createSegments()
    val segments_mu: ArrayList<List<Int>>  = averageSegmentColor()


    private fun construction(): Array<Direction> {
        // TODO: Implement heuristic construction of the chromosome. Now it's just random.
        // Like Minimal Spanding Tree, use Prims algorithm
        val possibleDirections = listOf<Direction>(Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP)
        val randChromosome = Array(geneSize) { possibleDirections.random() }
        // test chromosome
        /*val test = Array(geneSize) { Direction.NONE }
        test[0] = Direction.RIGHT
        test[1] = Direction.RIGHT
        test[2] = Direction.DOWN
        test[3] = Direction.DOWN
        test[4] = Direction.LEFT
        test[5] = Direction.NONE
        test[6] = Direction.RIGHT
        test[7] = Direction.RIGHT
        test[8] = Direction.RIGHT
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


    fun createSegments(): ArrayList<MutableSet<Int>> {
        /**
         * Creates the segments of the image by traversing the graph connecting the pixels
         * Does not pay attention to the direction of the edges.
         */
        val segments = ArrayList<MutableSet<Int>>()
        val unvisitedNodes = MutableList(geneSize) { it }

        while (unvisitedNodes.isNotEmpty()) {
            val node = unvisitedNodes.removeFirst()

            val segment = getConnectedNodes(node)
            if (segment.isEmpty())
                segments.add(mutableSetOf(node))
            else
                segments.add(segment)

            unvisitedNodes.removeAll(segment)
        }
        return segments

    }
    fun getConnectedNodes(startPos: Int, visited: MutableSet<Int> = mutableSetOf<Int>()): MutableSet<Int> {
        val connections = mutableSetOf<Int>()

        //if (chromosome[startPos] == Direction.NONE) {
        //    return connections.toMutableList()
        //}
        var i = startPos
        if (i !in visited)
            connections.add(i)

        //println()

        do {
            val next = getNextNode(i)
            visited.add(i)

            //println("Standing in node: $i")
            //println("\tVisited: ${visited.toList()}")
            //println("\tConnections: ${connections.toList()}")
            //println("\tNext node: $next")

            // If the node on the left points RIGHT
            if ((i - 1) >= 0) {
                if (chromosome[i-1] == Direction.RIGHT && i-1 !in visited)  // not visited before
                    connections.add(i-1)
            }
            // If the node on the right points LEFT
            if ((i + 1) < this.geneSize ) {
                if (chromosome[i + 1] == Direction.LEFT && i+1 !in visited)
                    connections.add(i + 1)

            }
            // If the node on the top points DOWN
            if ((i - this.imgWidth) >= 0) {
                if (chromosome[i - this.imgWidth] == Direction.DOWN && i-this.imgWidth !in visited)
                    connections.add(i - this.imgWidth)
            }
            // If the node on the bottom points UP
            if ((i + this.imgWidth) < this.geneSize) {
                if (chromosome[i + this.imgWidth] == Direction.UP && i+this.imgWidth !in visited)
                    connections.add(i + this.imgWidth)
            }

            if (next in visited) {
                if (connections.size == 1)
                    return connections
                else
                    break
            }
            // If the next node was not added, add it
            if (next !in connections)
                connections.add(next)

            i = next
        } while (chromosome[next] != Direction.NONE && next !in visited)

        //println("Connections made: ${connections.toList()}")
        //println("Visited nodes: ${visited.toList()}")

        val unexploredConnections = connections.subtract(visited)

        //println("Unexplored connections: ${unexploredConnections.toList()}")
        for (node in unexploredConnections) {
            //println("\tExploring node: $node")
            val newNodes = getConnectedNodes(node, visited)
            //println("\tFrom exploring node: $node, new nodes found: ${newNodes.toList()}")
            connections.addAll(newNodes)
        }

        return connections //.toMutableList()
    }
    fun averageSegmentColor(): ArrayList<List<Int>> {
        /**
         * Calculates the average color of each segment.
         * Returns a hashmap with the segment number as key and the color as value.
         */
        val segmentColors = ArrayList<List<Int>>()
        for (segment in this.segments) {

            var red = 0
            var green = 0
            var blue = 0
            for (pixel in segment) {
                val rgb = image.getPixel(pixel)
                red += rgb[0]
                green += rgb[1]
                blue += rgb[2]
            }
            segmentColors.add(listOf(red / segment.size, green / segment.size, blue / segment.size))
            //segmentColors.add(Color(red / segment.size, green / segment.size, blue / segment.size))
        }
        return segmentColors
    }

    fun createSegments2(): ArrayList<MutableList<Int>> {
        /**
         * OLD VERSION
         * Creates the segments of the image by traversing the graph connecting the pixels
         * Does not pay attention to the direction of the edges.
         */
        val segments = ArrayList<MutableList<Int>>()
        val unvisitedNodes = MutableList(geneSize) { it }

        while (unvisitedNodes.isNotEmpty()) {
            val node = unvisitedNodes.removeFirst()

            val segment = getConnectedNodes2(node)
            if (segment.isEmpty())
                segments.add(mutableListOf(node))
            else
                segments.add(segment)

            unvisitedNodes.removeAll(segment)
        }
        return segments
        // this.segments.forEach { println(it) }
    }
    private fun getConnectedNodes2(i: Int, visited: MutableList<Int> = mutableListOf()): MutableList<Int> {
        /**
         * OLD VERSION
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

            val childConnections = getConnectedNodes2(node, visited)
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
        this.edgeFitness()
        this.connectivityFitness()
        this.overallDeviationFitness()
    }
    fun overallDeviationFitness() {
        /**
         * Measure of similarity in each segment
         * TODO: Investigate if calculating the distance for each color independently is more correct
         */
        var sum = 0.0
        for (segmentIdx in this.segments.indices) {
            val mu_rgb = this.segments_mu[segmentIdx]
            val segment = this.segments[segmentIdx]

            // The distance between the mean and every pixel in the segment
            for (i in segment) {
                val rgb_i = this.image.getPixel(i)
                sum += image.distance(rgb_i, mu_rgb)
            }
        }
        this.overallDeviation = sum
    }
    fun connectivityFitness() {
        /**
         * Measure of connectivity
         */
        val kernel = doubleArrayOf(
            1/8.0, 1/8.0, 1/8.0,
            1/8.0,   8.0, 1/8.0,
            1/8.0, 1/8.0, 1/8.0)

        var sum = 0.0
        for (segment in this.segments) {
            for (i in segment) {
                // Top left
                if ( (i-this.imgWidth-1) >= 0 && (i-this.imgWidth-1) !in segment)
                    sum += kernel[0]
                // Top
                if ( (i-this.imgWidth) >= 0 && (i-this.imgWidth) !in segment)
                    sum += kernel[1]
                // Top right
                if ( (i-this.imgWidth+1) >= 0 && (i-this.imgWidth+1) !in segment)
                    sum += kernel[2]
                // Left
                if ( (i-1) >= 0 && (i-1) !in segment)
                    sum += kernel[3]
                // Right
                if ( (i+1) >= 0 && (i+1) !in segment)
                    sum += kernel[5]
                // Bottom left
                if ( (i+this.imgWidth-1) >= 0 && (i+this.imgWidth-1) !in segment)
                    sum += kernel[6]
                // Bottom
                if ( (i+this.imgWidth) >= 0 && (i+this.imgWidth) !in segment)
                    sum += kernel[7]
                // Bottom right
                if ( (i+this.imgWidth+1) >= 0 && (i+this.imgWidth+1) !in segment)
                    sum += kernel[8]
            }
        }

        this.connectivity = sum
    }
    fun edgeFitness() {
        /**
         * Measures the clearness of the boundaries in the image.
         * Should be maximized but to keep the similarity between the objectives,
         *    it is minimized here by negating it.
         */
        var sum = 0.0
        for (segment in this.segments) {

            for (i in segment) {
                // If the node on the RIGHT is in a different segment
                if (i + 1 < this.geneSize && i+1 !in segment) {
                    sum += image.distance(i, i+1)
                }
                // If the node UNDER is in a different segment
                if (i + this.imgWidth < this.geneSize && i+this.imgWidth !in segment) {
                    sum += image.distance(i, i+this.imgWidth)
                }
            }
        }
        this.edgeValue = sum
    }

    fun printInfo() {
        println("\nIndividual:")
        //print("\tChromosome:")
        //for (i in this.chromosome.indices) {
        //    if (i % this.imgWidth == 0)
        //        println()
        //    print("\t%-6s".format(this.chromosome[i]))
        //}
        //println("\n")
        println("\tSegments: ${this.segments.size}")
        println("\tFitness:")
        println("\t\tEdge: $edgeValue")
        println("\t\tConnectivity: $connectivity")
        println("\t\tOverall Deviation: $overallDeviation")

        // println("\tSegments mu: ${this.segments_mu.toList()}")
    }
}
