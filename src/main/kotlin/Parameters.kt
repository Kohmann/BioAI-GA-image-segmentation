class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 10
    val mutationRate: Double = 0.005
    val crossoverRate: Double = 0.7
    val numGenerations: Int = 50

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = true
    val edgeWeight = 0.01
    val connectivityWeight = 1
    val deviationWeight = 0.05

}