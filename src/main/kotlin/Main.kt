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
    val params = Parameters() // parameters that control the program

    val systemPath = System.getProperty("user.dir")
    val imageFolder = params.imageFolder // folder to read images from, training_image and test_images
    val workingImageFolder = params.workingImageFolder // for example "86016"

    //val imgPath = "$systemPath/src/main/resources/$imageFolder/$workingImageFolder/Test image.jpg"
    val imgPath = "$systemPath/src/evaluator/$imageFolder/$workingImageFolder/Test image.jpg"
    val savePath = "$systemPath/src/evaluator/Student_Segmentation_Files/$workingImageFolder/"
    println("Working on image: $imgPath")

    val image = ImageObject(File(imgPath), savePath=savePath)

    // starts the program and also times the execution
    val GA = GeneticAlgorithm(image)

    measureTimeMillis {
        if (params.simpleGA)
            GA.runGA()
        else
            GA.runNSGA()

    }.let {
        println("\nTime taken: $it ms, ${it / 1000} s")
    }




}
