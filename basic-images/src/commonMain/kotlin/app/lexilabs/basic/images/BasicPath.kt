package app.lexilabs.basic.images

/**
 * Represents a local file path for an image.
 *
 * This class is used to distinguish between a URL and a local file path.
 * It encapsulates the string representation of the path.
 *
 * @param pathString The absolute path of the file.
 *
 * Example:
 * ```kotlin
 * val path = BasicPath("appLocalDirectory/cacheDirectory/images/exampleImage.jpeg")
 * println(path.toString())
 * ```
 */
@ExperimentalBasicImages
public class BasicPath(pathString: String) {
    private val path: String = pathString

    /**
     * Returns the string representation of the file path.
     */
    override fun toString(): String {
        return path
    }
}