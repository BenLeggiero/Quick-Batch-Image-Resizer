package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.FileOrImage.*
import QuickBatchImageResizer.ImageDropTarget.State.*
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.*
import javafx.event.EventHandler
import javafx.scene.input.*
import javafx.scene.input.TransferMode.*
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import java.awt.*
import java.awt.datatransfer.DataFlavor.*
import java.io.*
import javax.swing.*
import kotlin.properties.*

/**
 * @author Ben Leggiero
 * @since 2018-05-12
 */
class ImageDropTarget(var delegate: Delegate): Pane() {

    var state: State by Delegates.observable(inactive as State) { _,_,_ ->
        updateUi()
    }

    init {
        onDragEntered = userDidStartDrag()
        onDragOver = userDidContinueDrag()
        onDragDropped = userDidDragDrop()

        this.setMinSize(128.0, 128.0)
        this.setPrefSize(256.0, 256.0)

        updateUi()
    }


    private fun updateUi() {
        border = Border(BorderStroke(state.paint, BorderStrokeStyle.DASHED, CornerRadii(4.0), BorderWidths(4.0)))
    }


    fun userDidStartDrag() = EventHandler<DragEvent> {
        println("Drag started: $it")
        it.acceptTransferModes(COPY)

        state = hovering(setOf())
    }


    fun userDidCancelDrag() = EventHandler<DragEvent> {
        println("Drag cancelled: $it")
        state = inactive
    }


    fun userDidContinueDrag() = EventHandler<DragEvent> {
        println("Drag continued: $it")
        it.acceptTransferModes(COPY)
    }


    fun userDidDragDrop() = EventHandler<DragEvent> {
        println("Drag drop: $it")

        state = holding(setOf())
    }

    inner class FileTransferHandler : TransferHandler() {

        val supportedDataFlavors = setOf(imageFlavor, javaFileListFlavor)

        override fun canImport(support: TransferSupport?): Boolean {
            return super.canImport(support)
                    && support?.dataFlavors?.containsAny(supportedDataFlavors)
                    ?: false
        }


        override fun importData(support: TransferSupport?): Boolean {

            val transferrable = support?.transferable

            state = when (transferrable) {
                null -> inactive
                is Image -> dropping(setOf(image(transferrable)))
                is File -> dropping(setOf(file(transferrable)))
                is List<*> -> dropping(
                        transferrable
                                .mapNotNull { it as? File }
                                .map { file(it) }
                                .toSet()
                )
                else -> inactive
            }

            return super.importData(support)
        }
    }



    sealed class State(val paint: Paint) {
        object inactive: State(MaterialColors.blueGrey100)
        object denying: State(MaterialColors.red600)

        sealed class StateWithFiles(val fileOrImages: Set<FileOrImage>, paint: Paint): State(paint) {
            /** When the user has is holding the image(s) atop drop target */
            class hovering(files: Set<FileOrImage>) : StateWithFiles(files, MaterialColors.lightBlue400)

            /** When the user has just let go of the image(s), but they are not yet held by the drop target */
            class dropping(files: Set<FileOrImage>) : StateWithFiles(files, MaterialColors.lightBlue400)

            /** When the image drop target is holding the image(s) */
            class holding(files: Set<FileOrImage>) : StateWithFiles(files, MaterialColors.blueGrey500)

            val filesString get() = when (fileOrImages.count()) {
                0 -> "🚫"
                1 -> fileOrImages.firstOrNull()?.name ?: "🚫"
                2 -> fileOrImages.joinToString(" and ") { it.name }
                else -> fileOrImages.joinToString(", ") { it.name }
            }
        }
    }



    sealed class FileOrImage {
        class file(val file: File): FileOrImage()
        class image(val image: Image): FileOrImage()

        val name: String get() = when (this) {
            is file -> this.file.name
            is image -> "🖼"
        }
    }



    interface Delegate {
        /**
         * Called to determine whether the drop target should accept or reject the given item
         */
        fun shouldAcceptDrop(item: FileOrImage): Boolean


        /**
         * Called after a drop was successfully completed
         */
        fun didReceiveDrop(item: FileOrImage): DropReaction
    }


    enum class DropReaction {
        processingNotStarted,
        processingSucceeded,
        processingFailed,
        ;
    }
}

private fun <T> Array<T>.containsAny(others: Iterable<T>): Boolean = this.intersect(others).isNotEmpty()
