
class Population(private var populationSize: Int,
                 image: ImageObject) {

    val parents = ArrayList<Individual>()
    val offspring = ArrayList<Individual>()
    val individuals = ArrayList<Individual>()

    var fronts = ArrayList<ArrayList<Individual>>()

    init {
        repeat(populationSize) {
            parents.add(Individual(image))
        }
    }

    fun combineWithOffspring() {
        individuals.addAll(parents)
        individuals.addAll(offspring)
    }

    fun calculateFitness() {
        individuals.forEach {
            it.calculateFitnesses()
        }
    }

    fun assignRank() {
        /**
         * Assigns rank to each individual with respect to dominance in the objective fitness space.
         * https://link.springer.com/content/pdf/10.1007/3-540-45356-3.pdf   On pdf page: 857
         */
        var rank = 1 // which panotofront we are working on
        val unassignedIndividuals = individuals.toMutableList() // copy of individuals
        val rankedIndividuals = ArrayList<ArrayList<Individual>>() // list of panotofronts
        while (unassignedIndividuals.isNotEmpty()) {
            val dominatingSet = findDominatingSet(unassignedIndividuals)
            println("Dominating set: ${dominatingSet.size}")
            dominatingSet.forEach { it.assignRank(rank) }
            unassignedIndividuals.removeAll(dominatingSet)
            rankedIndividuals.add(dominatingSet) // adds each panotofront to rankedIndividuals

            rank++
        }

        // Updates the population with the ranked individuals
        individuals.clear()
        individuals.addAll(rankedIndividuals.flatten())
        this.fronts = rankedIndividuals // save globally for later
    }
    fun findDominatingSet(individuals: MutableList<Individual>): ArrayList<Individual> {
        /**
         * Sorts the individuals in the population according to non-domination.
         * https://cs.uwlax.edu/~dmathias/cs419/readings/NSGAIIElitistMultiobjectiveGA.pdf
         */
        val nonDominatingSet = ArrayList<Individual>()
        val dominatedSet = HashSet<Individual>() // for speed

        //nonDominatingSet.add(individuals[0])

        for (individual in individuals) {
            if (dominatedSet.contains(individual)) // already dominated
                continue
            nonDominatingSet.add(individual)
            for (otherIndividual in nonDominatingSet) {

                if (individual in dominatedSet || individual == otherIndividual) { // already in non-dominating set
                    continue
                }
                if (individual.dominates(otherIndividual)) {
                    dominatedSet.add(otherIndividual)
                }
                else if (otherIndividual.dominates(individual)) {
                    dominatedSet.add(individual)
                    break
                }
            }
        }
        nonDominatingSet.removeAll(dominatedSet)
        return nonDominatingSet
    }

    fun determineCrowdingDistance(front: ArrayList<Individual>): ArrayList<Individual> {
        /**
         * Calculates the crowding distance for each individual in the front.
         */
        // for each objective
        for (objective in 0 until front[0].fitnesses().size) {
            front.sortBy { it.fitnesses()[objective] } // sort by objective, ascending

            // edges of the panotofront is set to infinite
            front[0].crowdingDistance = Double.MAX_VALUE
            front[front.size - 1].crowdingDistance = Double.MAX_VALUE
            val fmax = front[front.size - 1].fitnesses()[objective]
            val fmin = front[0].fitnesses()[objective]
            for (i in 1 until front.size - 1) {
                front[i].crowdingDistance +=
                    (front[i + 1].fitnesses()[objective] - front[i - 1].fitnesses()[objective]) / (fmax - fmin)
            }
        }
        return front
    }

    fun selection() {
        val newPopulation = ArrayList<Individual>()
        val frontNr = 0
        while (newPopulation.size < populationSize) {

            // If space for the whole front, add it
            if (newPopulation.size + fronts[frontNr].size <= populationSize) {
                newPopulation.addAll(fronts[frontNr])
            }
            else { // otherwise, add the individuals with the highest crowding distance
                val frontWithDistance = determineCrowdingDistance(fronts[frontNr])
                frontWithDistance.sortBy { it.crowdingDistance }
                newPopulation.addAll(frontWithDistance.subList(0, populationSize - newPopulation.size))
            }
        }
        individuals.clear()
        parents.clear()
        parents.addAll(newPopulation)
    }
    fun createOffspring(mutationRate:Double) {
        /**
         * Creates the offspring of the parents.
         * TODO: make it proper
         */
        val newPopulation = ArrayList<Individual>()
        while (newPopulation.size < populationSize) {
            val parent1 = parents[0]
            val parent2 = parents[1]
            val child = parent1.crossover(parent2)
            child.mutate(mutationRate)
            newPopulation.add(child)
        }
        offspring.clear()
        offspring.addAll(newPopulation)
    }
}
