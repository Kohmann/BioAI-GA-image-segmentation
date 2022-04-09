
class Population(populationSize: Int,
                 image: ImageObject) {

    val individuals = ArrayList<Individual>()
    private val offspring = ArrayList<Individual>()

    init {
        for (i in 0 until populationSize) {
            individuals.add(Individual(image))
        }
    }

    fun calculateFitnesses() {
        individuals.forEach {
            it.calculateFitnesses()
        }
    }

    fun selection() {
        //TODO("not implemented")
    }
    fun assignRank(): Individual {
        //TODO("not implemented")
        return individuals[0]
    }
    fun crossover(): Individual {
        //TODO("not implemented")
        return individuals[0]
    }

    fun mutate(mutationRate: Double) {
        individuals.forEach {
            it.mutate(mutationRate)
        }
    }

}
