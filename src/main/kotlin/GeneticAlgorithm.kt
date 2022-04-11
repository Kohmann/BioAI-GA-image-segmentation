



class GeneticAlgorithm(image: ImageObject) {

    // PARAMETERS
    private val populationSize: Int = 10
    private val mutationRate: Double = 0.005
    private val crossoverRate: Double = 0.7
    private val numGenerations: Int = 10


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

    }

}
