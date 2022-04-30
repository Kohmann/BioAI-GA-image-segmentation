class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 20
    val mutationRate: Double = 0.1 // currently only act as a bool for doing JoinSegments or not
    val crossoverRate: Double = 0.7 //
    val numGenerations: Int = 30

    val minimalSegmentSize = 100 // Experimental parameter

    val save_results = true

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = true
    val edgeWeight = 1.0
    val connectivityWeight = 10.0
    val deviationWeight = 1.0

}