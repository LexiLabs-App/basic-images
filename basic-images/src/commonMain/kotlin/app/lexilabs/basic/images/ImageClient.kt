package app.lexilabs.basic.images

import app.lexilabs.basic.logging.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

/**
 * A Ktor HTTP client for downloading images from a URL.
 *
 * This object handles the network request and provides the image data as a [ByteArray].
 * It includes basic error handling for common HTTP status codes.
 *
 * Note: Caching is configured but currently disabled.
 *
 * Example:
 * ```kotlin
 * val imageData = ImageClient("https://example.com/image.png")
 * if (imageData != null) {
 *     // Use the image data
 * }
 * ```
 */
@ExperimentalBasicImages
public object ImageClient {

    private const val TAG = "ImageClient"

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
        install(HttpCache) {
//            val cacheFile = Files.createDirectories(Paths.get("build/cache")).toFile()
//            privateStorage(FileStorage(cacheFile)) // You have to create a custom class using interface [CacheStorage]
        }
    }

    /**
     * Asynchronously downloads an image from the given [urlString].
     *
     * This function performs a GET request and handles the following HTTP status codes:
     * - 200 (OK)
     * - 404 (Not Found)
     * - 429 (Rate Limit)
     * - 500 (Server Error)
     *
     * @param urlString The URL of the image to download.
     * @return A [ByteArray] containing the image data if the request is successful, otherwise `null`.
     */
    public suspend operator fun invoke(urlString: String): ByteArray? {
        return try {
            val response: HttpResponse = client.get(urlString)
            when (response.status.value) {
                200 -> {
                    /** OK **/
                    Log.d(TAG, "Response [${response.status.value}]: OK - $urlString")
                    response.bodyAsChannel().readRemaining().readByteArray()
                }

                404 -> {
                    /** Not Found **/
                    Log.e(TAG, "Response [${response.status.value}]: Not Found - $urlString")
                    null
                }

                429 -> {
                    /** Rate Limit **/
                    Log.e(TAG, "Response [${response.status.value}]: Rate Limit - $urlString")
                    null
                }

                500 -> {
                    /** Server Error **/
                    Log.e(TAG, "Response [${response.status.value}]: Server Error - $urlString")
                    null
                }

                else -> {
                    /** Unknown Error **/
                    Log.e(TAG, "Response [${response.status.value}]: ${response.status.description} - $urlString")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            null
        }
    }
}