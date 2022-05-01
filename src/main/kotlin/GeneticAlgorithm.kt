import kotlin.system.measureTimeMillis


class GeneticAlgorithm(private val image: ImageObject) {

    // PARAMETERS
    var params = Parameters()

    private val populationSize: Int = params.populationSize
    private val mutationRate: Double = params.mutationRate
    private val crossoverRate: Double = params.crossoverRate
    private val numGenerations: Int = params.numGenerations


    val population = Population(populationSize, image)
    private var generation = 0

    fun runNSGA() {

        repeat(numGenerations) {
            println("Generation: $generation")
            val start = System.currentTimeMillis()

            population.combineWithOffspring() // combines parents with offspring

            // Non Dominated Sorting
            population.evaluate()

            println("\tUnique individuals: ${population.individuals.map { it.hashCode() }.toSet().size} of ${population.individuals.size}")

            val segmentCount = population.individuals.map { it.segments.flatten().sum() }
            if (segmentCount.any { it != segmentCount[0]})
                throw IllegalStateException("Bugged individual, segment count is not equal")

            val segmentSizes = population.individuals.map { it.segments.size }
            println("\tAverage segment number: ${segmentSizes.average()}")

            population.assignRank()
            population.selection() // finds all parent candidates

            population.createOffspring(mutationRate, crossoverRate)

            val end = System.currentTimeMillis()
            println("\tTime used in ${end - start}ms, ${(end - start) / 1000}s")

            generation++
        }

        population.combineWithOffspring()
        population.evaluate()
        population.assignRank()
        population.stopThreads()
        println("Best individuals:")
        if (params.save_results) {
            val optimalIndividuals = population.fronts[0].filter {
                it.segments.size <= params.maxNumberOfSegments && it.segments.size >= params.minNumberOfSegments }

            image.saveAll(optimalIndividuals.toSet(), mode="black")
            image.saveAll(optimalIndividuals.toSet(), mode="green")
        }


        println("Connectivity")
        population.fronts[0].forEach {
            print("\t, ${it.connectivity}")
        }
        println("\nEdge")
        population.fronts[0].forEach {
            print("\t, ${it.edgeValue}")
        }
        println("\nOverall")
        population.fronts[0].forEach {
            print("\t, ${it.overallDeviation}")
        }

    }

    fun runGA() {
        var prevBestFitness = 0.0
        repeat(numGenerations) {
            print("Generation: $generation")
            val start = System.currentTimeMillis()

            population.combineWithOffspring() // combines parents with offspring
            population.evaluate()
            val generationBest = population.bestIndividual().weightedFitness
            print("\t Best: %-10.2f  Fitness improvement: %-10.2f".format(generationBest, (prevBestFitness - generationBest) ))
            print(", Segments: ${population.bestIndividual().segments.size}")

            // save every 1/5th generation of total generations
            //if (generation % (numGenerations * 0.25).toInt() == 0)
            //    image.save(population.bestIndividual(), mode="green", extra_info = "_generation=%d".format(generation)) // saving as image, black or green

            prevBestFitness = generationBest
            population.selectionGA() // finds all parent candidates
            population.createOffspring(mutationRate, crossoverRate)

            val end = System.currentTimeMillis()
            println(", Time used in ${end - start}ms, ${(end - start) / 1000}s")
            generation++
        }
        population.combineWithOffspring()
        population.evaluate()
        population.stopThreads()
        val best = population.bestIndividual()
        println("Best individual:")
        best.printInfo()
        if (params.save_results) {
            val extra_information = "_final"
            //image.saveAll(setOf(best) , mode="black", extra_info=extra_information)
            image.save(best, mode="black", extra_info=extra_information) // saving as image, black or green
            image.save(best, mode="green", extra_info=extra_information) // saving as image, black or green
        }

    }

}
