import java.io.File
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis


/**
 * Main class that initiates the ants
 */

fun main(args: Array<String>) {

    val systemPath = System.getProperty("user.dir")
    val imgPath = "$systemPath/src/main/resources/training_images/118035/Test image.jpg"

    val image = ImageObject(File(imgPath))

    // starts the program and also times the execution
    val GA = GeneticAlgorithm(image)
    measureTimeMillis {
        GA.population.individuals[0].createSegments()

    }.let {
        println("Time taken: $it ms, ${it / 1000} s")
    }

    // GA.run()
    // println(GA.population.individuals[0].segment_mu)
    //println(GA.population.individuals[0].segments.size)



}
