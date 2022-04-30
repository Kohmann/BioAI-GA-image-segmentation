class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 20
    val mutationRate: Double = 0.2
    val crossoverRate: Double = 0.5
    val numGenerations: Int = 30

    val minimalSegmentSize = 100 // Experimental parameter

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = false
    val edgeWeight = 1.0
    val connectivityWeight = 10.0
    val deviationWeight = 1.0

}