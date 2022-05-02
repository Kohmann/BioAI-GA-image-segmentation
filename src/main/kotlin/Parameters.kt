class Parameters {

    val imageFolder = "training_images"
    val workingImageFolder = "118035"

    // Genetic algorithm parameters
    val populationSize: Int = 30
    val mutationRate: Double = 0.2 // currently only act as a bool for doing JoinSegments or not
    val crossoverRate: Double = 0.7 //
    val numGenerations: Int = 10

    // When saving the images, save only those that satisfy the following conditions
    val minNumberOfSegments = 4
    val maxNumberOfSegments = 50

    val minimalSegmentSize = 10 // not used anymore

    val save_results = true

    // Parameters for individuals
    val useMST = true

    val simpleGA: Boolean = true
    val edgeWeight = 1.0
    val connectivityWeight = 500.0
    val deviationWeight = 2.0

}