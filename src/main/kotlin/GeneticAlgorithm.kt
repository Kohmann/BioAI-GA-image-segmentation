



class GeneticAlgorithm(private val image: ImageObject) {

    // PARAMETERS
    var params = Parameters()

    private val populationSize: Int = params.populationSize
    private val mutationRate: Double = params.mutationRate
    private val crossoverRate: Double = params.crossoverRate
    private val numGenerations: Int = params.numGenerations


    val population = Population(populationSize, image)
    private var generation = 0

    fun runNSGA() {

        repeat(numGenerations) {
            println("Generation: $generation")

            population.combineWithOffspring() // combines parents with offspring
            // Non Dominated Sorting
            population.calculateFitness()
            population.assignRank()

            population.selection() // finds all parent candidates
            population.createOffspring(mutationRate, crossoverRate)

            generation++
        }
        population.combineWithOffspring()
        population.calculateFitness()
        population.assignRank()
        println("Best connectivity individuals:")
        population.fronts[0].forEach {
            println("\t${it.segments.size}")
            image.save(it, "green") // saving as image, black or green
        }
    }

    fun runGA() {
        var prevBestFitness = 10000000.0
        repeat(numGenerations) {
            println("Generation: $generation")

            population.combineWithOffspring() // combines parents with offspring
            population.calculateFitness()
            val generationBest = population.bestIndividual().weightedFitness
            println("\tCurrent best: %-10.2f  Fitness improvement: %-6.2f".format(generationBest, (prevBestFitness - generationBest) ))
            prevBestFitness = generationBest
            population.selectionGA() // finds all parent candidates
            population.createOffspring(mutationRate, crossoverRate)

            generation++
        }
        population.combineWithOffspring()
        population.calculateFitness()
        val best = population.bestIndividual()
        println("Best individual:")
        best.printInfo()
        image.save(best, "green") // saving as image, black or green

    }

}
