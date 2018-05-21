@file:Suppress("PackageDirectoryMismatch")

package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.DropReaction.*
import QuickBatchImageResizer.ImageDropTarget.FileOrImage
import QuickBatchImageResizer.ImageDropTarget.State.*
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.holding
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.hovering
import QuickBatchImageResizer.Utilities.nonEmpty
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.CacheHint.QUALITY
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode.COPY
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight.BOLD
import javafx.scene.text.TextAlignment.CENTER
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.properties.Delegates


/**
 * @author Ben Leggiero
 * @since 2018-05-12
 */
class ImageDropTarget(var delegate: Delegate): BorderPane() {

    var state: State by Delegates.observable(inactive as State) { _,_,_ ->
        updateUi()
    }

    val callToAction: Label = {
        val label = Label("Drop images here")
        label.isWrapText = true
        label.textAlignment = CENTER
        label.font = Font.font(Font.getDefault().family, BOLD, 16.0)
        /*return*/ label
    }()

    init {
        onDragEntered = userDidStartDrag()
        onDragExited = userDidCancelDrag()
        onDragOver = userDidContinueDrag()
        onDragDropped = userDidDragDrop()

        this.setMinSize(128.0, 128.0)
        this.setPrefSize(256.0, 256.0)

        center = callToAction
        this.padding = Insets(8.0)

        updateUi()
    }


    private fun updateUi() {
        val paint = state.paint
        border = Border(BorderStroke(paint, BorderStrokeStyle.DASHED, CornerRadii(4.0), BorderWidths(4.0)))
        callToAction.text = state.callToAction
        callToAction.textFill = paint
    }


    fun userDidStartDrag() = EventHandler<DragEvent> { dragEvent ->
        println("Drag started: $dragEvent")

        val filesOrImages = extractFilesOrImages(from = dragEvent, using = delegate)

        if (null != filesOrImages) {
            state = hovering(filesOrImages)
            dragEvent.acceptTransferModes(COPY)
        }
        else {
            state = denying
        }

        dragEvent.consume()
    }

    private fun extractFilesOrImages(from: DragEvent,
                                     using: Delegate): Set<FileOrImage>? {

        return FileOrImage.extractAll(from).let { extractedFilesOrImages ->
            extractedFilesOrImages.nonEmpty()?.let {
                using.shouldAcceptDrop(it)
            }
        }
                ?.nonEmpty()
    }


    private fun userDidCancelDrag() = EventHandler<DragEvent> {
        println("Drag cancelled: $it")
        state = when (state) {
            is holding -> state
            else -> inactive
        }
        it.consume()
    }


    private fun userDidContinueDrag() = EventHandler<DragEvent> { dragEvent ->
        println("Drag continued: $dragEvent")

        if (state !== denying) {
            dragEvent.acceptTransferModes(COPY)
        }

        dragEvent.consume()
    }


    private fun userDidDragDrop() = EventHandler<DragEvent> { dragEvent ->
        println("Drag drop: $dragEvent")

        val filesOrImages = extractFilesOrImages(from = dragEvent, using = delegate)

        if (null != filesOrImages) {
            state = holding(filesOrImages)
            dragEvent.acceptTransferModes(COPY)
            val reaction = delegate.didReceiveDrop(filesOrImages)

            when (reaction) {
                accepted -> state = holding(filesOrImages)
                processing -> state = StateWithFiles.processing(filesOrImages)
                rejected -> state = denying
            }
        }
        else {
            state = inactive
        }

        dragEvent.consume()
    }

    fun clear() {
        state = inactive
    }

//    inner class FileTransferHandler : TransferHandler() {
//
////        val supportedDataFlavors = setOf(imageFlavor, javaFileListFlavor)
////
////        override fun canImport(support: TransferSupport?): Boolean {
////            return super.canImport(support)
////                    && support?.dataFlavors?.containsAny(supportedDataFlavors)
////                    ?: false
////        }
//
//
//        override fun importData(support: TransferSupport?): Boolean {
//
//            val transferrable = support?.transferable
//
//            state = when (transferrable) {
//                null -> inactive
//                is Image -> processing(setOf(image(transferrable)))
//                is File -> processing(setOf(file(transferrable)))
//                is List<*> -> processing(
//                        transferrable
//                                .mapNotNull { it as? File }
//                                .map { file(it) }
//                                .toSet()
//                )
//                else -> inactive
//            }
//
//            return super.importData(support)
//        }
//    }



    sealed class State(val paint: Paint,
                       val callToAction: String) {
        object inactive: State(MaterialColors.blueGrey100, "Drop images here")
        object denying: State(MaterialColors.red600, "🚫 Not that")

        sealed class StateWithFiles(val fileOrImages: Set<FileOrImage>, paint: Paint, callToAction: String): State(paint, callToAction) {
            /** When the user has is holding the image(s) atop drop target */
            class hovering(images: Set<FileOrImage>) : StateWithFiles(images, MaterialColors.lightBlue400, "Yeah that 🙌")

            /** When the user has just let go of the image(s), but they are not yet held by the drop target */
            class processing(images: Set<FileOrImage>) : StateWithFiles(images, MaterialColors.lightBlue400, "Working on it...")

            /** When the image drop target is holding the image(s) */
            class holding(images: Set<FileOrImage>) : StateWithFiles(images, MaterialColors.blueGrey500, filesString(images))

            companion object {
                fun filesString(fileOrImages: Set<FileOrImage>) = when (fileOrImages.count()) {
                    0 -> "🚫"
                    1 -> fileOrImages.firstOrNull()?.name ?: "🚫"
                    2 -> fileOrImages.joinToString(" and ", transform = { it.name })
                    else -> fileOrImages.joinToString(", ", transform = { it.name })
                }
            }
        }
    }



    sealed class FileOrImage {

        class file(val file: File): FileOrImage()
        class image(val image: Image, val originalFile: File? = null): FileOrImage()



        val name: String get() = when (this) {
            is file -> this.file.name
            is image -> this.originalFile?.name ?: "🖼"
        }


        @Throws(IOException::class)
        fun resized(newSize: Dimension2D): image = when (this) {
            is image -> image(this.image.resized(newSize = newSize))
            is file -> image(this.image?.resized(newSize = newSize)
                                     ?: throw IOException("Could not read ${this.file}"), originalFile = this.file)
        }


        companion object {
            fun extractAll(dragEvent: DragEvent): Set<FileOrImage> {
                val dragboard = dragEvent.dragboard
                val all = mutableSetOf<FileOrImage>()

                val files = dragboard.files
                if (null != files) {
                    files.forEach { all.add(file(it)) }
                }
                else {
                    dragboard.image?.let { all.add(image(it)) }
                }
                return all
            }
        }
    }



    interface Delegate {
        /**
         * Called to determine whether the drop target should accept or reject the given items
         *
         * @return The items that are accepted
         */
        fun shouldAcceptDrop(items: Set<FileOrImage>): Set<FileOrImage>


        /**
         * Called after a drop was successfully completed
         */
        fun didReceiveDrop(items: Set<FileOrImage>): DropReaction
    }


    enum class DropReaction {
        accepted,
        processing,
        rejected,
        ;
    }
}

class UnwrittenImage(val failedFile: File, val origin: FileOrImage)


fun Image.resized(newSize: Dimension2D, preserveRatio: Boolean = false): Image
        = ImageView(this).apply {
            isPreserveRatio = preserveRatio
            isSmooth = true
            fitWidth = newSize.width
            fitHeight = newSize.height
            isCache = false
            cacheHint = QUALITY
            isCache = true
        }
        .snapshot(null, null)


val FileOrImage.file.image: Image? get()
        = try { Image(this.file.path) }
        catch (_: Throwable) { null }


fun FileOrImage.image.write(to: File): UnwrittenImage? {
    val file = to
    return try {
        ImageIO.write(image.buffered(), format, file)
        null
    } catch (error: Throwable) {
        UnwrittenImage(failedFile = file, origin = this)
    }
}


@Suppress("NOTHING_TO_INLINE")
inline fun Image.buffered(): BufferedImage = SwingFXUtils.fromFXImage(this, null)


private fun <T> Array<T>.containsAny(others: Iterable<T>): Boolean = this.intersect(others).isNotEmpty()
