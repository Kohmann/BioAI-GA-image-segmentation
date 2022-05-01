class Parameters {

    val imageFolder = "training_images"
    val workingImageFolder = "118035"

    // Genetic algorithm parameters
    val populationSize: Int = 20
    val mutationRate: Double = 0.05 // currently only act as a bool for doing JoinSegments or not
    val crossoverRate: Double = 0.7 //
    val numGenerations: Int = 5

    // When saving the images, save only those that satisfy the following conditions
    val minNumberOfSegments = 4
    val maxNumberOfSegments = 50


    val minimalSegmentSize = 10 // not used anymore

    val save_results = true

    // Parameters for individuals
    val useMST = true

    val simpleGA: Boolean = false
    val edgeWeight = 3.0
    val connectivityWeight = 100.0
    val deviationWeight = 10.0

}