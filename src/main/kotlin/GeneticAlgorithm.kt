



class GeneticAlgorithm(image: ImageObject) {

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
        population.individuals.forEach {
            if (it.rank == 1)
                it.printInfo()
        }

    }

}
