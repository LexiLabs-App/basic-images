package app.lexilabs.basic.images

/**
 * Represents a URL for an image.
 *
 * This class is used to distinguish between a URL and a local file path.
 * It encapsulates the string representation of the URL.
 *
 * @param urlString The URL of the image.
 *
 * Example:
 * ```kotlin
 * val url = BasicUrl("https://picsum.photos/200")
 * println(url.toString())
 * ```
 */
@ExperimentalBasicImages
public class BasicUrl(urlString: String) {
    private val url: String = urlString

    /**
     * Returns the string representation of the URL.
     */
    override fun toString(): String {
        return url
    }
}