import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


/**
 * Image object taking care of all image related operations
 */
class ImageObject(file: File) {
    private val image: Raster = this.load(file)
    // private val imageDistances =

    private fun load(file: File): Raster {
        val image = ImageIO.read(file).raster
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
        val pixel_1 = toPixelPair(a)
        val pixel_2 = toPixelPair(b)
        val rgb_1 = getPixel(pixel_1.first, pixel_1.second)
        val rgb_2 = getPixel(pixel_2.first, pixel_2.second)
        var sum = 0.0
        for (i in 0 until 3) {
            val diff = rgb_1[i] - rgb_2[i]
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