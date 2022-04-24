import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.Kernel
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO


/**
 * Image object taking care of all image related operations
 */
class ImageObject(file: File,
                  private val savePath: String) {
    private val image: Raster = this.load(file)
    private val rawImage = ImageIO.read(file)

    // TODO: Maybe add some image processing options, like blurring,

    private fun load(file: File): Raster {
        val image = ImageIO.read(file).raster
        //val image = ImageIO.read(file).raster.createChild(0, 0, 100, 100, 0, 0, null)

        // x is in horisintal direction, y is in vertical direction
        val width = image.width
        val height = image.height
        val channels = image.numBands
        println("Image size: $width x $height x $channels")
        return image
    }
    private fun toPixelPair(n: Int): Pair<Int, Int> {
        // Remember: x is in horizontal direction, y is in vertical direction
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

    fun save(solution: Individual, mode: String, extra_info: String = "") {
        /**
         * Saves the image to a file
         * solution: ArrayList of MutableSets containing the indices of the pixels that should be painted
         * mode:
         *  - "black" - white background with black edges
         *  - "green" - RGB image with green edges
         */
        val folder = if (mode == "black") "type1" else "type2"

        val fullFilePath = savePath + folder + "/" + "TEST_" + "numSegments_" +
                           solution.segments.size.toString() +
                            extra_info + ".jpg"


        val imageFile = File(fullFilePath) // correct to
        val img = BufferedImage (image.width, image.height, BufferedImage.TYPE_INT_RGB)

        val edgeColor = when (mode) {
            "black" -> Color.BLACK
            "green" -> Color.GREEN
            else -> Color.BLACK
        }
        for (i in 0 until image.width * image.height) {
            val pixel = toPixelPair(i) // pixel coordinates, <int, int> : horizontal and vertical respectively
            if (solution.isEdge(i)) {
                img.setRGB(pixel.first, pixel.second, edgeColor.rgb) // set the pixel to the edge color
            } else {
                if (mode == "green") {
                    val originalColor = rawImage.getRGB(pixel.first, pixel.second) // get the original RGB color
                    img.setRGB(pixel.first, pixel.second, originalColor) // set the pixel to the original color
                } else {
                    img.setRGB(pixel.first, pixel.second, Color.WHITE.rgb) // make the pixel white
                }
            }
        }

        // border of the image
        for (i in 0 until image.width) {
            img.setRGB(i, 0, edgeColor.rgb)
            img.setRGB(i, image.height - 1, edgeColor.rgb)
        }
        for (i in 0 until image.height) {
            img.setRGB(0, i, edgeColor.rgb)
            img.setRGB(image.width - 1, i, edgeColor.rgb)
        }


        ImageIO.write(img, "jpg", imageFile)

    }
}