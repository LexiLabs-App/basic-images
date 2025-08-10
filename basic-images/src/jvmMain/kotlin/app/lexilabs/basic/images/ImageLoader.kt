package app.lexilabs.basic.images

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO

/**
 * JVM-specific implementation for loading images from a [BasicUrl] or [BasicPath].
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
     */
    public actual suspend fun load(url: BasicUrl): ImageBitmap? {
        var bitmap: ImageBitmap? = null
        return withContext(Dispatchers.IO) {
            ImageClient(url.toString())?.let { bitmapByteArray ->
                bitmap = bitmapByteArray.toImageBitmap()
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
     */
    public actual suspend fun load(path: BasicPath): ImageBitmap? {
        return withContext(Dispatchers.IO) {
            val bitmapByteArray = File(path.toString()).readBytes()
            return@withContext bitmapByteArray.toImageBitmap()
        }
    }

    /**
     * Converts a [ByteArray] to an [ImageBitmap].
     */
    private fun ByteArray.toImageBitmap(): ImageBitmap {
        // Convert ByteArray to BufferedImage
        val inputStream = ByteArrayInputStream(this)
        val bufferedImage: BufferedImage = ImageIO.read(inputStream)
        // Convert BufferedImage to ImageBitmap
        return bufferedImage.toComposeImageBitmap()
    }
}