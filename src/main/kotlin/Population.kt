import kotlin.random.Random

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
        individuals.clear()
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

            dominatingSet.forEach { it.assignRank(rank) }
            unassignedIndividuals.removeAll(dominatingSet)
            rankedIndividuals.add(dominatingSet) // adds each panotofront to rankedIndividuals

            rank++
        }

        // Updates the population with the ranked individuals
        individuals.clear()
        individuals.addAll(rankedIndividuals.flatten())
        this.fronts = rankedIndividuals // save globally for later
        for (i in 0 until fronts.size) {
            println("\tFront $i: ${fronts[i].size}")
        }
    }
    fun findDominatingSet(individualsSubset: MutableList<Individual>): ArrayList<Individual> {
        /**
         * Sorts the individuals in the population according to non-domination.
         * https://cs.uwlax.edu/~dmathias/cs419/readings/NSGAIIElitistMultiobjectiveGA.pdf
         */
        val nonDominatingSet = ArrayList<Individual>()
        val dominatedSet = HashSet<Individual>() // for speed

        for (individual in individualsSubset) {
            if (dominatedSet.contains(individual)) // already dominated
                continue
            nonDominatingSet.add(individual) // temporarily add as a non-dominated individual
            for (otherIndividual in nonDominatingSet) {

                if (dominatedSet.contains(individual) || individual == otherIndividual) { // already in non-dominating set
                    continue
                }
                else if (individual.dominates(otherIndividual)) {
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
            //println("\t\tObjective $objective:")
            //front.forEach { println("\t\t\t${it.fitnesses()[objective]}") }
            // edges of the panotofront is set to infinite

            front[0].crowdingDistance = Double.MAX_VALUE
            front[front.lastIndex].crowdingDistance = Double.MAX_VALUE

            val fmin = front.first().fitnesses()[objective] // lowest fitness value
            val fmax = front.last().fitnesses()[objective] // highest fitness value

            for (i in 1 until front.lastIndex) {
                front[i].crowdingDistance +=
                    (front[i + 1].fitnesses()[objective] - front[i - 1].fitnesses()[objective]) / (fmax - fmin)
            }
        }
        return front
    }

    fun selection() {
        val newPopulation = ArrayList<Individual>()
        var frontNr = 0

        while (newPopulation.size < populationSize) {

            // If space for the whole front, add it
            if (newPopulation.size + fronts[frontNr].size <= populationSize) {
                newPopulation.addAll(fronts[frontNr])
            }
            else { // otherwise, add the individuals with the highest crowding distance
                val frontWithDistance = determineCrowdingDistance(fronts[frontNr])
                frontWithDistance.sortBy { it.crowdingDistance }

                val startIndex = frontWithDistance.lastIndex - (populationSize - newPopulation.size)
                newPopulation.addAll(frontWithDistance.subList(startIndex, frontWithDistance.lastIndex))
            }
            frontNr++
        }

        individuals.clear()
        parents.clear()
        parents.addAll(newPopulation)
    }
    fun selectionGA() {
        val newPopulation = ArrayList<Individual>()

        while (newPopulation.size < populationSize) {
            val parent1 = individuals.random()
            var parent2 = individuals.random()
            while (parent1 == parent2)  parent2 = individuals.random()
            if (parent1.weightedFitness < parent2.weightedFitness) // Minimizing fitness
                newPopulation.add(parent1)
            else
                newPopulation.add(parent2)
        }
        individuals.clear()
        parents.clear()
        parents.addAll(newPopulation)
    }
    fun createOffspring(mutationRate:Double, crossoverRate:Double) {
        /**
         * Creates the offspring of the parents.
         * TODO: make it proper
         */
        val newPopulation = ArrayList<Individual>()
        while (newPopulation.size < populationSize) {
            val parent1 = parents.random()
            var parent2 = parents.random()
            while (parent2 == parent1) parent2 = parents.random()

            val children = crossover(parent1, parent2, crossoverRate)
            children.forEach { it.mutate(mutationRate) }
            newPopulation.addAll(children)

        }
        offspring.clear()
        offspring.addAll(newPopulation)
    }
    fun crossover(p1: Individual,p2: Individual, crossoverRate:Double): Array<Individual> {
        /**
         * Creates the offspring of the parents.
         */
        if (Random.nextFloat() < crossoverRate) {
            val children = p1.crossover(p2)
            return children
        }
        return arrayOf(p1, p2)
    }

    fun bestIndividual(): Individual {
        return individuals.sortedBy { it.weightedFitness }.first()
    }


}
