
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


/**
 * Image object taking care of all image related operations
 */
class ImageObject(file: File) {
    private val image: Raster = this.load(file)

    private fun load(file: File): Raster {
        val image = ImageIO.read(file).raster
        val width = image.width
        val height = image.height
        val channels = image.numBands
        println("Image size: $width x $height x $channels")
        return image
    }

    fun getPixel(x: Int, y: Int): List<Int> {
        /**
         * Returns the pixel at the given coordinates, x being the column and y the row
         * Returns: List of ints containing the pixel values for each channel, RGB,
         * Each color takes on a value 0-255
         */
        return image.getPixel(x, y, IntArray(image.numBands)).toList()
    }

}


/**
 * Main class that initiates the ants
 */

fun main(args: Array<String>) {
    println("Hello World!")
    val systemPath = System.getProperty("user.dir")
    val imgPath = "$systemPath/src/main/resources/training_images/118035/Test image.jpg"

    val image = ImageObject(File(imgPath))
    val pixel = image.getPixel(0, 0)
    println(pixel)


}
