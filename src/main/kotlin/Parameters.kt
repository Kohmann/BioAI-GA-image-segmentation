class Parameters {
    // Genetic algorithm parameters
    val populationSize: Int = 20
    val mutationRate: Double = 0.005
    val crossoverRate: Double = 0.7
    val numGenerations: Int = 50

    val minimalSegmentSize = 100 // Experimental parameter

    // Parameters for individuals

    val useMST = true

    val simpleGA: Boolean = false
    val edgeWeight = 0.01
    val connectivityWeight = 10.0
    val deviationWeight = 0.1

}