import java.awt.image.Kernel
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


/**
 * Image object taking care of all image related operations
 */
class ImageObject(file: File) {
    private val image: Raster = this.load(file)
    // TODO: Maybe add some image processing options, like blurring,

    private fun load(file: File): Raster {
        val image = ImageIO.read(file).raster
        //val image = ImageIO.read(file).raster.createChild(0, 0, 10, 10, 0, 0, null)

        val width = image.width
        val height = image.height
        val channels = image.numBands
        println("Image size: $width x $height x $channels")
        return image
    }
    private fun toPixelPair(n: Int): Pair<Int, Int> {
        val x = n % image.width
        val y = n / image.width
        return Pair(x, y)
    }
    fun distance(a: Int, b: Int): Double {
        /**
         * The Euclidean distance between two pixels in RGB space,
         * but first converts two positions to rgb
         */
        val pixel_1 = toPixelPair(a)
        val pixel_2 = toPixelPair(b)
        val rgb_1 = getPixel(pixel_1.first, pixel_1.second)
        val rgb_2 = getPixel(pixel_2.first, pixel_2.second)
        return distance(rgb_1, rgb_2)
    }
    fun distance(rgb1: List<Int>, rgb2: List<Int>): Double {
        /**
         * The Euclidean distance between two pixels in RGB space
         */
        var sum = 0.0
        for (i in 0 until 3) {
            val diff = rgb1[i] - rgb2[i]
            val square = diff * diff
            sum += square
        }
        return Math.sqrt(sum)
    }
    fun getHeight(): Int {
        return image.height
    }
    fun getWidth(): Int {
        return image.width
    }
    fun getChannels(): Int {
        return image.numBands
    }
    fun getPixel(n: Int): List<Int> {
        val pixel = toPixelPair(n)
        return getPixel(pixel.first, pixel.second)
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