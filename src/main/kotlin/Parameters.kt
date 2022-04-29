class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 10
    val mutationRate: Double = 0.07
    val crossoverRate: Double = 0.7
    val numGenerations: Int = 40

    val minimalSegmentSize = 100 // Experimental parameter

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = false
    val edgeWeight = 1.0
    val connectivityWeight = 10.0
    val deviationWeight = 1.0

}