
/**
 * Class representing an individual in the population.
 * Contains a chromosome and functions for calculating fitness, crossover and mutation.
 */
enum class Direction {
    LEFT, RIGHT, UP, DOWN, NONE
}

class Individual(private val geneSize: Int = 10) {
    private val chromosome: Array<Direction> = construction()

    private fun construction(): Array<Direction> {
        // TODO: Implement heuristic construction of the chromosome. Now it's just random.
        return Array(geneSize) { Direction.values().random() }
    }

}
