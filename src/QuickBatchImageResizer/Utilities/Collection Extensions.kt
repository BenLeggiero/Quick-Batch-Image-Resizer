package QuickBatchImageResizer.Utilities

/**
 * @author Ben Leggiero
 * @since 2018-05-20
 */

fun <C, E> C.nonEmpty(): C? where C: Collection<E> = if (this.isEmpty()) null else this
