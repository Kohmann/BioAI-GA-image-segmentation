import java.util.*
import javax.swing.text.Segment
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Class representing an individual in the population.
 * Contains a chromosome and functions for calculating fitness, crossover and mutation.
 */
enum class Direction {
    LEFT, RIGHT, UP, DOWN, NONE
}

class Individual(private val image: ImageObject,
                 initChromosome: Array<Direction> = Array(0){ Direction.NONE }) {

    private val params = Parameters()
    // Image information
    private val imgWidth = image.getWidth()
    private val imgHeight = image.getHeight()
    private val geneSize = imgHeight * imgWidth

    // Objective functions
    var createdSegments = false
    var evaluated = false
    var overallDeviation: Double = 0.0
    var connectivity: Double = 0.0
    var edgeValue: Double = 0.0
    var weightedFitness: Double = 0.0

    var rank: Int = 0
    var crowdingDistance: Double = 0.0

    private var chromosome: Array<Direction> = if (initChromosome.isEmpty()) construction() else initChromosome
    var segments: ArrayList<MutableSet<Int>> = arrayListOf() //createSegments()
    private var segments_mu: ArrayList<List<Int>> = arrayListOf() //averageSegmentColor()


    private fun construction(): Array<Direction> {
        /**
         * Initialize the chromosome with random directions or using Minimal Spanning Tree algorithm.
         */
        val possibleDirections = listOf<Direction>(Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP)

        val gene = if (params.useMST)
                    primMST()
                else
                    Array(geneSize) { possibleDirections.random() } // random initialization
        return correctChromosome(gene)
    }

    private fun primMST(): Array<Direction> {
        /**
         * Prim's algorithm, used to find the minimal spanning tree
         */
        var pos = Random.nextInt(0 until imgWidth * imgHeight)
        val initChromosome = Array(geneSize) { Direction.NONE }
        val visited = HashSet<Int>()
        val heap = PriorityQueue<Edge>()

        while (visited.size < geneSize){
            if (pos !in visited) {
                visited.add(pos)

                getNeighbors(pos).forEach { n ->
                    if (n !in visited) {
                        val edge = Edge(pos, n, image.distance(pos, n))
                        heap.add(edge)
                    } }
            }
            val bestEdge = heap.poll() // Get the edge with the minimal distance
            initChromosome[bestEdge.to] = setDirection(bestEdge.from, bestEdge.to) // Set the direction of the 'to' node
            pos = bestEdge.to
        }

        for (i in 0 until 2) {
            val randNode = Random.nextInt(0 until geneSize)
            initChromosome[randNode] = setDirection(randNode, randNode) // Set the direction of the 'to' node
        }

        return initChromosome
    }
    private fun setDirection(from: Int, to: Int): Direction {
        /**
         * Sets the direction of the 'to' node based on the 'from' node
         */
        return if (from -1 == to) {
            Direction.RIGHT
        } else if (from + 1 == to) {
            Direction.LEFT
        } else if (from - imgWidth == to) {
            Direction.DOWN
        } else if (from + imgWidth == to) {
            Direction.UP
        } else { // from == to
            Direction.NONE
        }
    }
    private fun getNeighbors(pos: Int): List<Int> {
        val neighbors = ArrayList<Int>()
        val x = pos % imgWidth
        val y = pos / imgWidth
        if (x > 0) {
            neighbors.add(pos - 1)
        }
        if (x < imgWidth - 1) {
            neighbors.add(pos + 1)
        }
        if (y > 0) {
            neighbors.add(pos - imgWidth)
        }
        if (y < imgHeight - 1) {
            neighbors.add(pos + imgWidth)
        }
        return neighbors
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
        // First, correct the chromosome
        this.chromosome = this.correctChromosome(this.chromosome)

        //println("chromosome as matrix")
        //for (i in 0 until this.chromosome.size) {
        //    if (i % this.imgWidth == 0) {
        //        println()
        //    }
        //    print("%-5s  ".format(this.chromosome[i].toString()))
        //}

        val segments = ArrayList<MutableSet<Int>>()
        val unvisitedNodes = MutableList(geneSize) { it }

        while (unvisitedNodes.isNotEmpty()) {
            val node = unvisitedNodes.removeFirst() // removeLast

            val segment = getConnectedNodes(node)
            if (segment.isEmpty())
                segments.add(mutableSetOf(node))
            segments.add(segment)
            //println("\nsegment: $segment\n")

            unvisitedNodes.removeAll(segment)
        }
        //println("All segments: $segments")
        // just a check to see if all nodes exist in a segment
        for (i in 0 until geneSize) {
            if (!segments.any { it.contains(i) }) {
                println("Node $i is not in any segment")
            }
        }

        return segments
    }
    fun getConnectedNodes(startPos: Int, visited: MutableSet<Int> = mutableSetOf<Int>()): MutableSet<Int> {
        val connections = mutableSetOf<Int>()

        var i = startPos
        if (i !in visited)
            connections.add(i)

        do {
            val next = getNextNode(i) //getNextChild(i)
            //val pointsTowardsMe = getNextChild(i, visited)
            //val next = if (pointsTowardsMe == i) getNextNode(i) else pointsTowardsMe

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
    fun getNextChild(i: Int, visited: MutableSet<Int>): Int {
        // Return the index of a child that points to me (i)
        val neighbourNodes = getNeighbors(i).toMutableSet().subtract(visited)
        for (node in neighbourNodes) {
            val direction = setDirection(i, node)
            if (chromosome[node] == direction)
                return node
        }
        return i //getNextNode(i)
    }

    fun isEdge(i: Int): Boolean {
        // Return true if the node has neighbours in another segment
        val commonSegment = segments.single { it.contains(i) }

        return if (i + 1 !in commonSegment && i + 1 < this.geneSize)
            true
        else i + this.imgWidth !in commonSegment && i + this.imgWidth < this.geneSize
    }

    fun crossover(parentB: Individual): Array<Individual> {
        /**
         * Crossover two individuals.
         * Returns a list of two new individuals.
         * More crossovers to come
         */
        if (Random.nextDouble() < 0.5)
            return onePointCrossover(parentB)
        else
            return mergeSectorCrossover(parentB)

    }
    private fun onePointCrossover(parentB: Individual): Array<Individual> {
        /**
         * Crossover two individuals.
         * Returns a list of two new individuals.
         * More crossovers to come
         */
        val childA = Array<Direction>(this.geneSize) { Direction.NONE }
        val childB = Array<Direction>(this.geneSize) { Direction.NONE }

        val crossoverPoint = Random.nextInt(1, this.geneSize)

        for (i in 0 until crossoverPoint) {
            childA[i] = this.chromosome[i]
            childB[i] = parentB.chromosome[i]
        }
        for (i in crossoverPoint until this.geneSize) {
            childA[i] = parentB.chromosome[i]
            childB[i] = this.chromosome[i]
        }

        return arrayOf(Individual(image, initChromosome = childA), Individual(image, initChromosome = childB))
    }
    private fun mergeSectorCrossover(parentB: Individual): Array<Individual> {
        val childA = Array<Direction>(this.geneSize) { Direction.NONE }
        val childB = Array<Direction>(this.geneSize) { Direction.NONE }
        /**
         * We have to make copies not to change initial individuals
         */
        val parentACopy = this
        val parentBCopy = parentB

        for (i in 0 until 10){
            val crossoverPoint = Random.nextInt(1, this.geneSize)
            var segment1 = mutableSetOf<Int>()
            var segment2 = mutableSetOf<Int>()
            for (segment in this.segments){
                if (crossoverPoint in segment) {
                    segment1 = segment
                    break
                }
            }
            for (segment in parentB.segments){
                if (crossoverPoint in segment) {
                    segment2 = segment
                    break
                }
            }
            val newSegment = segment1.union(segment2).toMutableList()
            /**
             * We merge indices of 2 segments to which breakpoint belongs
             * We get a unified segment
             * We change parent A and parent B so that
             * They are of the form oooooooooooooo[segment starts....breakpoint......segment ends]ttttttttttttttttt
             * we create kids just as in onePOintCrossover because the merged segments are now the same in both
             */
            parentACopy.segmentToGraph(newSegment)
            parentBCopy.segmentToGraph(newSegment)

            for (x in 0 until crossoverPoint) {
                childA[x] = parentACopy.chromosome[x]
                childB[x] = parentBCopy.chromosome[x]
            }
            for (y in crossoverPoint until this.geneSize) {
                childA[y] = parentBCopy.chromosome[y]
                childB[y] = parentACopy.chromosome[y]
            }

        }
        return arrayOf(Individual(image, initChromosome = childA), Individual(image, initChromosome = childB))
    }


    private fun uniformCrossover(parentB: Individual): Array<Individual> {
        val chromosomeA = this.chromosome.clone()
        val chromosomeB = parentB.chromosome.clone()

        for (i in 0 until this.geneSize) {
            if (Random.nextDouble() < 0.5)
                chromosomeA[i] = parentB.chromosome[i]
            else
                chromosomeB[i] = this.chromosome[i]
        }

        return arrayOf(Individual(this.image, initChromosome = chromosomeA), Individual(this.image, initChromosome = chromosomeB))
    }
    private fun joinSegments() {
        /**
         * Combines two random neighbouring segments into one.
         */
        if (this.segments.size < 3) return
        //println("number of segments before: ${this.segments.size}")
        val segmentNr = Random.nextInt(this.segments.size-1)
        //println("segmentNr: $segmentNr")
        val segmentA = this.segments.removeAt(segmentNr) //all elements after segmentNr are shifted to the left
        val segmentB = this.segments.removeAt(segmentNr)
        val newSegment = segmentA.union(segmentB).toMutableList()

        // Ensures that the edges of the given segment are connected in the chromosome
        segmentToGraph(newSegment)
    }
    private fun joinSegmentSearch() {
        /**
         * Combines two neighbouring segments which are similar into one.
         * Should be doublechecked if it works properly.
         */
        // There exists a segment that can be joined
        if (this.segments.size < 5) return

        //this.segments.sortedBy { it.size }.first()
        val segmentIndex = Random.nextInt(this.segments.size-1)
        val segment = this.segments.removeAt(segmentIndex)
        val segmentColor = this.segments_mu.removeAt(segmentIndex)

        val edgeNeighbours = getAllEdgeNeighbours(segment)
        var mostEqualSegment = Pair<MutableSet<Int>, Double>(mutableSetOf(), Double.MAX_VALUE)

        val nodesInCheckedSegments = mutableSetOf<Int>()

        // for every nodes that is in a possible neighbour segment
        for (neighbour in edgeNeighbours) {

            // filter out thise nodes that are already in a segment or are already in a checked segment
            if (neighbour !in segment && neighbour !in nodesInCheckedSegments) {
                // a candidate neighbour segment
                // println("Neighbour: $neighbour")
                // val candidateSegment = this.segments.firstOrNull { it.contains(neighbour) } ?: throw Error("Candidate segment is null")
                val candidateSegmentIndex = this.segments.indexOfFirst { it.contains(neighbour) }
                if (candidateSegmentIndex == -1) throw Error("Candidate segment is null")
                val candidateSegment = this.segments.removeAt(candidateSegmentIndex)
                val candidateSegmentColor = this.segments_mu.removeAt(candidateSegmentIndex)


                // does not need to check all the other nodes in this segment
                nodesInCheckedSegments.addAll(candidateSegment)

                // Distance between the two segments, closer the better
                val colorSimilarity = image.distance(segmentColor, candidateSegmentColor)
                if (colorSimilarity < mostEqualSegment.second)
                    mostEqualSegment = Pair(candidateSegment, colorSimilarity)
            }
        }

        val newSegment = segment.union(mostEqualSegment.first).toMutableList()

        //this.segments.add(newSegment.toMutableSet())
        // Ensures that the edges of the given segment are connected in the chromosome
        segmentToGraph(newSegment)

    }
    private fun segmentToGraph(segment: MutableList<Int>) {
        /**
         * Converts a segment to a connected graph in the chromosome.
         */

        //this.segments.add(segment.toMutableSet())
        while (segment.isNotEmpty()) {
            val i = segment.removeFirst()

            val neightbours = getNeighbors(i) // get the neighbours of the node
            for (neighbour in neightbours) {
                if (neighbour in segment) { // selects the first neighbour that is in the segment
                    this.chromosome[neighbour] = setDirection(i, neighbour)
                    break
                }
            }
        }
    }

    private fun mergeSmallSegments() {
        /**
         * Merges small segments into one.
         * MAY NOT WORK PROPERLY.
         */
        val smallSegments:List<MutableSet<Int>> = this.segments.filter { it.size <= params.minimalSegmentSize }
        this.segments.removeAll(smallSegments)


        val visitedOrMerged = HashSet<MutableSet<Int>>()

        // for every small segment
        for (segment in smallSegments) {
            val edgeNeighbours = getAllEdgeNeighbours(segment)

            // for every neighbour node
            for (neighbour in edgeNeighbours) {

                // find the segment that contains the neighbour
                var neighbourSegment: MutableSet<Int>? = null
                for (smallseg in smallSegments)
                    if (neighbour in smallseg) {
                        neighbourSegment = smallseg
                        break
                    }
                if (neighbourSegment == null) continue // should not happen but just in case


                if (neighbourSegment !in visitedOrMerged) {
                    visitedOrMerged.add(neighbourSegment)

                    val newSegment = segment.union(neighbourSegment).toMutableList()
                    // Ensures that the edges of the given segment are connected in the chromosome
                    segmentToGraph(newSegment)
                    break
                }
            }
        }
    }
    private fun getAllEdgeNeighbours(a: MutableSet<Int>): Set<Int> {
        /**
         * Returns a list of all neighbouring nodes that are not in current segment.
         */
        val edgeNeighbours = mutableSetOf<Int>()
        for (node in a) {
            val candidates = getNeighbors(node).filter { it !in a }
            if (candidates.isNotEmpty()) {
                edgeNeighbours.addAll(candidates)
            }
        }
        return edgeNeighbours
    }
    fun mutate(mutationRate: Double) {
        /**
         * Mutates the chromosome at random.
         * More implementation to come.
         */
        if (mutationRate > 0.0) {
            if (Random.nextDouble() < mutationRate) {
                when (Random.nextInt(0,1)) {
                    0 -> randomMutation(mutationRate)
                    //else -> joinSegmentSearch()
                    else -> joinSegments()
                }
            }

            createdSegments = false
            evaluated = false
        }
    }

    fun update() {
        this.segments = createSegments() // update the segments
        this.segments_mu = averageSegmentColor() // update the mean segments
        this.createdSegments = true
        this.evaluated = false
    }

    fun setCrowdingDist(crowdingDistance: Double) {
        this.crowdingDistance = crowdingDistance
    }
    private fun randomMutation(mutationRate: Double) {
        val possibleDirections = setOf<Direction>(Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP)
        val randomIndex = Random.nextInt(chromosome.size)
        chromosome[randomIndex] =  possibleDirections.subtract(setOf(chromosome[randomIndex])).random() // Only new directions
    }

    fun returnReverse(dir:Direction): Direction{
        if (dir == Direction.DOWN){
            return Direction.UP
        }
        if (dir == Direction.UP){
            return Direction.DOWN
        }
        if (dir == Direction.RIGHT){
            return Direction.LEFT
        }
        if (dir == Direction.LEFT){
            return Direction.RIGHT
        }
        return Direction.NONE

    }
    /** reverses some number of directions inside a chromosome */
    fun reversePointMutation(mutationRate:Double) {
        /** manually assign the highest possible number of reverses */
        val NUMREVERSES = 100
        val times = (0..NUMREVERSES).random().toInt()
        for(i in  0 until times){
            val index = (0..geneSize).random().toInt()
            chromosome[index] = returnReverse(chromosome[index])
        }
    }
    fun crazyMutation(mutationRate: Double) {
        /**
         * We randomly choose a cube size
         * We randomly find  indices from which we take those
         * We transpose those values
         */
        if (mutationRate < 0.0001){
            val row = (0..imgHeight).random()
            val column = (0..imgWidth).random()
            val min = minOf(imgWidth - column, imgHeight - row)
            val size = (2..min).random()
            transposeSquare(row, column, size)

        }
    }
    fun transposeSquare(row:Int, column:Int, size:Int){
        val matrix = MutableList<MutableList<Direction>>(0) { mutableListOf(Direction.NONE) }
        for (i in row until row + size){
            val vector = MutableList<Direction>(0) { Direction.NONE }
            for (j in column until column + size){
                vector.add(chromosome[i*imgHeight + imgWidth])
            }
            matrix.add(vector)
        }

        for (i in 0 until size){
            for (j in 0 until size) {
                chromosome[(row + i)*imgHeight + column + j] = matrix[j][i]
            }
        }

    }

    fun crowdingTournamentSelection(other: Individual): Individual {
        return if (this.rank < other.rank)
            this
        else if (this.rank > other.rank)
            other
        else if (this.crowdingDistance > other.crowdingDistance)
            this
        else
            other
    }
    fun dominates(other: Individual): Boolean {
        return this.connectivity < other.connectivity
                && this.edgeValue < other.edgeValue
                && this.overallDeviation < other.overallDeviation
    }
    fun assignRank(rank: Int) {
        this.rank = rank
    }
    fun calculateFitnesses() {
        // Calculate the fitness for each objective
        this.edgeFitness()
        this.connectivityFitness()
        this.overallDeviationFitness()
        // if weights for the objectives are determined
        if (this.params.simpleGA)
            this.weightedFitness = this.combinedFitness()

        this.evaluated = true
    }
    fun combinedFitness(): Double {
        return this.edgeValue * this.params.edgeWeight +
                this.connectivity * this.params.connectivityWeight +
                this.overallDeviation * this.params.deviationWeight

    }
    fun fitnesses(): List<Double> {
        return listOf(this.edgeValue, this.connectivity, this.overallDeviation)
    }
    fun overallDeviationFitness() {
        /**
         * Measure of similarity in each segment
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

                //sum /= kernel[4]
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
        this.edgeValue = -sum
    }

    fun printInfo() {
        println("\nIndividual:")
        println("\tSegments: ${this.segments.size}")
        println("\tRank: ${this.rank}")
        println("\tFitness:")
        println("\t\tEdge: %.4f".format(edgeValue.toFloat()))
        println("\t\tConnectivity: %.4f".format(connectivity.toFloat()))
        println("\t\tOverall Deviation: %.4f".format(overallDeviation.toFloat()))
    }

    override fun hashCode() = this.chromosome.toString().hashCode()
    fun copy() = Individual(image, chromosome)

}
