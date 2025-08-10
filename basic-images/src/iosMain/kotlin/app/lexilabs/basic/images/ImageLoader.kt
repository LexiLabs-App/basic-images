package app.lexilabs.basic.images

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRelease
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.create
import platform.UIKit.UIImage

/**
 * iOS-specific implementation for loading images from a [BasicUrl] or [BasicPath].
 * This object provides functions to load images and convert them into an [ImageBitmap].
 */
@Suppress("unused")
@OptIn(ExperimentalForeignApi::class, ExperimentalBasicImages::class)
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
//        var bitmap: ImageBitmap? = null
        return withContext(Dispatchers.IO) {
            getFileAsImageBitmap(path.toString())
        }
    }

    /**
     * Reads a file from the given [filePath] and converts it to an [ImageBitmap].
     */
    private fun getFileAsImageBitmap(filePath: String): ImageBitmap? {
        val fileManager = NSFileManager.defaultManager
        val nsData = fileManager.contentsAtPath(filePath)
        return nsData?.toUIImage()?.toImageBitmap()
    }

    /**
     * Converts a [ByteArray] to an [ImageBitmap].
     */
    private fun ByteArray.toImageBitmap(): ImageBitmap? {
        return this@toImageBitmap.toNSData().toUIImage().toSkiaImage()?.toComposeImageBitmap()
    }

    /**
     * Converts a [ByteArray] to an [NSData] object.
     */
    @OptIn(BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData {
        memScoped {
            return NSData.create(
                bytes = allocArrayOf(this@toNSData),
                length = this@toNSData.size.toULong()
            )
        }
    }

    /**
     * Converts an [NSData] object to a [ByteArray].
     */
    private fun NSData.toByteArray(): ByteArray {
        return this.bytes()?.readBytes(this.length.toInt()) ?: ByteArray(size = 0)
    }

    /**
     * Converts an [NSData] object to a [UIImage].
     */
    private fun NSData.toUIImage(): UIImage {
        return UIImage(this@toUIImage)
    }

    /**
     * Converts a [UIImage] to an [ImageBitmap].
     */
    private fun UIImage.toImageBitmap(): ImageBitmap {
        val skiaImage = this.toSkiaImage() ?: return ImageBitmap(1, 1)
        return skiaImage.toComposeImageBitmap()
    }

    /**
     * Converts a [UIImage] to a Skia [Image].
     */
    private fun UIImage.toSkiaImage(): Image? {
        val imageRef = this.CGImage ?: return null

        val width = CGImageGetWidth(imageRef).toInt()
        val height = CGImageGetHeight(imageRef).toInt()

        val bytesPerRow = CGImageGetBytesPerRow(imageRef)
        val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
        val bytePointer = CFDataGetBytePtr(data)
        val length = CFDataGetLength(data)

        val alphaType = when (CGImageGetAlphaInfo(imageRef)) {
            CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst,
            CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL
            CGImageAlphaInfo.kCGImageAlphaFirst,
            CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL
            CGImageAlphaInfo.kCGImageAlphaNone,
            CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst,
            CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE
            else -> ColorAlphaType.UNKNOWN
        }

        val byteArray = ByteArray(length.toInt()) { index ->
            bytePointer!![index].toByte()
        }

        CFRelease(data)
        CGImageRelease(imageRef)

        val skiaColorSpace = ColorSpace.sRGB
        val colorType = ColorType.RGBA_8888

        // Convert RGBA to BGRA
        for (i in byteArray.indices step 4) {
            val r = byteArray[i]
            val g = byteArray[i + 1]
            val b = byteArray[i + 2]
            val a = byteArray[i + 3]

            byteArray[i] = b
            byteArray[i + 2] = r
        }

        return Image.makeRaster(
            imageInfo = ImageInfo(
                width = width,
                height = height,
                colorType = colorType,
                alphaType = alphaType,
                colorSpace = skiaColorSpace
            ),
            bytes = byteArray,
            rowBytes = bytesPerRow.toInt(),
        )
    }
}