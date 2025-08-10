package app.lexilabs.basic.images

import androidx.compose.ui.graphics.ImageBitmap

/**
 * An `expect` object for loading images from a [BasicUrl] or [BasicPath].
 *
 * This object declares the platform-agnostic API for loading images.
 * The actual implementation is provided by `actual` objects on each platform.
 */
@Suppress("unused")
@ExperimentalBasicImages
public expect object ImageLoader {
    /**
     * Asynchronously loads an image from a [BasicUrl].
     *
     * @param url The [BasicUrl] of the image to load.
     * @return The loaded [ImageBitmap], or `null` if the image could not be loaded.
     */
    public suspend fun load(url: BasicUrl): ImageBitmap?

    /**
     * Asynchronously loads an image from a local [BasicPath].
     *
     * @param path The [BasicPath] of the image to load.
     * @return The loaded [ImageBitmap], or `null` if the image could not be loaded.
     */
    public suspend fun load(path: BasicPath): ImageBitmap?
}