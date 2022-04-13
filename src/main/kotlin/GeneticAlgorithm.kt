



class GeneticAlgorithm(image: ImageObject) {

    // PARAMETERS
    private val populationSize: Int = 20
    private val mutationRate: Double = 0.005
    private val crossoverRate: Double = 0.7
    private val numGenerations: Int = 100


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
        population.calculateFitness()
        population.assignRank()
        println("Best connectivity individuals: ${population.individuals.forEach { if (it.rank == 1)
            println(it.connectivity)
        }}")
        println("Best fitness individuals: ${population.individuals.forEach { if (it.rank == 1)
            println(it.printInfo())
        }}")
    }

}
