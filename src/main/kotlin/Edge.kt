data class Edge(val from: Int,
                val to: Int,
                val weight: Double): Comparable<Edge> {

    override fun compareTo(other: Edge): Int {
        if (this.weight < other.weight) return -1
        if (this.weight > other.weight) return 1
        return 0
    }
}