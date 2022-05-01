import java.awt.image.Kernel
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis


/**
 * Main class that runs the program
 */


fun main(args: Array<String>) {

    val systemPath = System.getProperty("user.dir")
    val workingImageFolder = "86016"
    val imgPath = "$systemPath/src/main/resources/training_images/$workingImageFolder/Test image.jpg"

    val savePath = "$systemPath/src/evaluator/Student_Segmentation_Files/"

    val image = ImageObject(File(imgPath), savePath=savePath)

    // starts the program and also times the execution
    val GA = GeneticAlgorithm(image)
    val params = Parameters()
    measureTimeMillis {
        if (params.simpleGA)
            GA.runGA()
        else
            GA.runNSGA()

    }.let {
        println("\nTime taken: $it ms, ${it / 1000} s")
    }




}
