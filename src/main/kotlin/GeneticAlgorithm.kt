



class GeneticAlgorithm(image: ImageObject) {

    // PARAMETERS
    private val populationSize: Int = 1
    private val mutationRate: Double = 0.01
    private val numGenerations: Int = 10


    val population = Population(populationSize, image)
    private var generation = 0

    fun run() {
        repeat(numGenerations) {
            println("Generation: $generation")
            population.assignRank()
            population.selection()
            population.crossover()
            population.mutate(mutationRate)
            generation++
        }
        println("Done")
    }

}
