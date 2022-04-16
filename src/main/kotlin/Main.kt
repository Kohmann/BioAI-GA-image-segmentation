import java.awt.image.Kernel
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis


/**
 * Main class that initiates the ants
 */

fun main(args: Array<String>) {

    val systemPath = System.getProperty("user.dir")
    val imgPath = "$systemPath/src/main/resources/training_images/118035/Test image.jpg"

    val savePath = "$systemPath/src/evaluator/Student_Segmentation_Files/"

    val image = ImageObject(File(imgPath), savePath=savePath)

    // starts the program and also times the execution
    val GA = GeneticAlgorithm(image)

    measureTimeMillis {
        GA.runNSGA()
        //GA.runGA()
    }.let {
        println("Time taken: $it ms, ${it / 1000} s")
    }




}
