package app.lexilabs.basic.images

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Android-specific implementation for loading images from a [BasicUrl] or [BasicPath].
 * This object provides functions to load images and convert them into an [ImageBitmap].
 */
@OptIn(ExperimentalBasicImages::class)
public actual object ImageLoader {

    /**
     * Loads an image from the given [url] and converts it to an [ImageBitmap].
     *
     * This function is asynchronous and should be called from a coroutine.
     * It uses [Dispatchers.IO] to perform the network request off the main thread.
     *
     * @param url The [BasicUrl] of the image to load.
     * @return The loaded [ImageBitmap], or `null` if the image could not be loaded.
     *
     * Example:
     * ```kotlin
     * val url = BasicUrl("https://picsum.photos/200")
     * val bitmap = ImageLoader.load(url)
     * ```
     */
    public actual suspend fun load(url: BasicUrl): ImageBitmap? {
        var bitmap: ImageBitmap? = null
        return withContext(Dispatchers.IO) {
            ImageClient(url.toString())?.let { bitmapByteArray ->
                bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.size)
                    .asImageBitmap()
            } ?: {
                bitmap = null
            }
            return@withContext bitmap
        }
    }

    /**
     * Loads an image from the given local file [path] and converts it to an [ImageBitmap].
     *
     * This function is asynchronous and should be called from a coroutine.
     * It uses [Dispatchers.IO] to perform the file reading off the main thread.
     *
     * @param path The [BasicPath] of the image to load.
     * @return The loaded [ImageBitmap], or `null` if the image could not be loaded.
     *
     * Example:
     * ```kotlin
     * val path = BasicPath("appLocalDirectory/cacheDirectory/images/exampleImage.jpeg")
     * val bitmap = ImageLoader.load(path)
     * ```
     */
    public actual suspend fun load(path: BasicPath): ImageBitmap? {
        var bitmap: ImageBitmap?
        return withContext(Dispatchers.IO) {
            val bitmapByteArray = File(path.toString()).readBytes()
            bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.size)
                .asImageBitmap()
            return@withContext bitmap
        }
    }
}