package app.lexilabs.basic.images

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import app.lexilabs.basic.logging.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

@OptIn(ExperimentalBasicImages::class)
public actual object ImageLoader {

    private val tag = "ImageLoader"
    private val client = HttpClient()

    /**
     * Asynchronously loads an image from the given [url] and converts it to an [ImageBitmap].
     *
     * @param url The [BasicUrl] of the image to load.
     * @return The loaded [ImageBitmap], or `null` if the image could not be loaded or decoded.
     */
    public actual suspend fun load(url: BasicUrl): ImageBitmap? {
        return withContext(Dispatchers.Default) {
            try {
                val bytes = client.get(url.toString()).body<ByteArray>()
                Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } catch (e: Exception) {
                Log.e(tag, "load:failure: $e")
                null
            }
        }
    }


    public actual suspend fun load(path: BasicPath): ImageBitmap? {
        // In a browser environment, a "local path" is treated as a resource
        // relative to the application's origin URL. We can use the same Ktor
        // client to fetch it as we do for remote URLs.
        return withContext(Dispatchers.Default) {
            try {
                // Fetch the resource from the path relative to the app's origin.
                val bytes = client.get(path.toString()).body<ByteArray>()
                // Decode the byte array into a Skia Image and convert to a Compose ImageBitmap.
                Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } catch (e: Exception) {
                Log.e(tag, "load:failure: $e")
                null
            }
        }
    }
}
