package app.lexilabs.basic.images

/**
 * Represents the different states of image loading for a [BasicImage].
 */
@ExperimentalBasicImages
public sealed class ImageState {

    /**
     * The initial state before any loading has started.
     */
    public data object NONE: ImageState()

    /**
     * The state while the image is being downloaded or loaded from a local path.
     */
    public data object LOADING: ImageState()

    /**
     * The state when the image has been successfully loaded and is being displayed.
     */
    public data object SHOWING: ImageState()

    /**
     * The state when an error has occurred during image loading.
     *
     * @param message A descriptive error message.
     */
    public data class ERROR(val message: String) : ImageState()
}