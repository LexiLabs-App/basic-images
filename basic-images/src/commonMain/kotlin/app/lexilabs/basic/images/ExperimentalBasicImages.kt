package app.lexilabs.basic.images

/**
 * Marks declarations that are part of the experimental Basic Images API.
 *
 * These APIs are not yet stable and may change in the future.
 * By using them, you agree to the possibility of breaking changes.
 */
@RequiresOptIn(message = "This API is experimental, unstable, and may change.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class ExperimentalBasicImages()