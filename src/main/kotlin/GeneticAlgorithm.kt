



class GeneticAlgorithm(image: ImageObject) {

    // PARAMETERS
    private val populationSize: Int = 5
    private val mutationRate: Double = 0.01
    private val numGenerations: Int = 10


    val population = Population(populationSize, image)
    private var generation = 0

    fun run() {
        repeat(numGenerations) {
            println("Generation: $generation")
            population.selection()

            population.mutate(mutationRate)
            generation++
        }
        println("Done")
    }

}
