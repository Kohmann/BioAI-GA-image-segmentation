import java.awt.image.Kernel
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
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

    /*
    val individuals = ArrayList<Individual>()
    repeat(40) {
        individuals.add(Individual(image))
    }

    val executor = Executors.newFixedThreadPool(8 )
    Thread.sleep(100)
    println("Starting")
    repeat(10){
        Thread.sleep(1000)
        measureTimeMillis {
            val futures = ArrayList<Future<Unit>>()
            for (individual in individuals) {
                futures.add(
                    CompletableFuture.supplyAsync(
                        { individual.calculateFitnesses() }, executor))
            }
            futures.forEach { it.get() }
            //individuals.forEach { it.calculateFitnesses() }

        }.let {
            println("Time taken: $it ms, ${it / 1000} s")
        }
    }
    executor.shutdownNow()

     */



}
