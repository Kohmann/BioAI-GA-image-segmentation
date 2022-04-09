import java.io.File
import kotlin.system.measureTimeMillis


/**
 * Main class that initiates the ants
 */

fun main(args: Array<String>) {
    println("Hello World!")
    val systemPath = System.getProperty("user.dir")
    val imgPath = "$systemPath/src/main/resources/training_images/118035/Test image.jpg"

    val image = ImageObject(File(imgPath))

    val GA = GeneticAlgorithm(image)
    // GA.run()


}
