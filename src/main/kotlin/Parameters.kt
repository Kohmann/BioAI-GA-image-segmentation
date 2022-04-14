class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 10
    val mutationRate: Double = 0.005
    val crossoverRate: Double = 0.7
    val numGenerations: Int = 10

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = false
    val edgeWeight = 2.0
    val connectivityWeight = 20.0
    val deviationWeight = 4.0

}