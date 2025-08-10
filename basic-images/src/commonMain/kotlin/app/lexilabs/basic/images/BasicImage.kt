package app.lexilabs.basic.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import app.lexilabs.basic.images.ImageLoader.load

/**
 * A composable that asynchronously loads and displays an image from a local [BasicPath].
 *
 * While the image is loading, a circular progress indicator can be displayed.
 * The state of the image loading is tracked by [ImageState].
 *
 * @param path The [BasicPath] of the image to be loaded.
 * @param contentDescription A description of the image for accessibility services.
 * @param modifier [Modifier] for this composable.
 * @param alignment The alignment of the image within its bounds.
 * @param contentScale The scaling of the image within its bounds.
 * @param alpha The opacity of the image.
 * @param colorFilter A [ColorFilter] to apply to the image.
 * @param filterQuality The [FilterQuality] to apply when scaling the image.
 * @param placeholderEnabled Whether to show a [CircularProgressIndicator] while loading.
 */
@ExperimentalBasicImages
@Composable
public fun BasicImage(
    path: BasicPath,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    placeholderEnabled: Boolean = true
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var state by remember { mutableStateOf<ImageState>(ImageState.NONE) }

    LaunchedEffect(path) {
        state = ImageState.LOADING
        bitmap = load(path)
        state = if (bitmap == null) {
            ImageState.ERROR("Bitmap failed during load")
        } else {
            ImageState.SHOWING
        }
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            modifier = modifier
        )
    } ?: if (placeholderEnabled) { BasicImagePlaceHolder(modifier)} else {}
}

/**
 * A composable that asynchronously loads and displays an image from a [BasicUrl].
 *
 * While the image is loading, a circular progress indicator can be displayed.
 * The state of the image loading is tracked by [ImageState].
 *
 * @param url The [BasicUrl] of the image to be loaded.
 * @param contentDescription A description of the image for accessibility services.
 * @param modifier [Modifier] for this composable.
 * @param alignment The alignment of the image within its bounds.
 * @param contentScale The scaling of the image within its bounds.
 * @param alpha The opacity of the image.
 * @param colorFilter A [ColorFilter] to apply to the image.
 * @param filterQuality The [FilterQuality] to apply when scaling the image.
 * @param placeholderEnabled Whether to show a [CircularProgressIndicator] while loading.
 */
@ExperimentalBasicImages
@Composable
public fun BasicImage(
    url: BasicUrl,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    placeholderEnabled: Boolean = true
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var state by remember { mutableStateOf<ImageState>(ImageState.NONE) }

    LaunchedEffect(url) {
        state = ImageState.LOADING
        bitmap = load(url)
        state = if (bitmap == null) {
            ImageState.ERROR("Bitmap failed during load")
        } else {
            ImageState.SHOWING
        }
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            modifier = modifier
        )
    } ?: if (placeholderEnabled) { BasicImagePlaceHolder(modifier)} else {}
}

/**
 * A composable that displays a [CircularProgressIndicator] as a placeholder.
 *
 * This is shown while a [BasicImage] is loading.
 *
 * @param modifier [Modifier] for this composable.
 */
@ExperimentalBasicImages
@Composable
public fun BasicImagePlaceHolder(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}