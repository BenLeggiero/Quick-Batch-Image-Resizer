package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.State.*
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.*
import javafx.event.*
import javafx.geometry.*
import javafx.scene.control.*
import javafx.scene.image.*
import javafx.scene.input.*
import javafx.scene.input.TransferMode.*
import javafx.scene.layout.*
import javafx.scene.paint.*
import javafx.scene.text.*
import javafx.scene.text.FontWeight.*
import javafx.scene.text.TextAlignment.*
import java.io.*
import kotlin.properties.*

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

        FileOrImage(dragEvent)?.let {
            if (delegate.shouldAcceptDrop(it)) {
                state = hovering(it)
                dragEvent.acceptTransferModes(COPY)
            }
            else {
                state = denying
            }
        }

        dragEvent.consume()
    }


    fun userDidCancelDrag() = EventHandler<DragEvent> {
        println("Drag cancelled: $it")
        state = when (state) {
            is holding -> state
            else -> inactive
        }
        it.consume()
    }


    fun userDidContinueDrag() = EventHandler<DragEvent> { dragEvent ->
        println("Drag continued: $dragEvent")

        if (state !== denying) {
            dragEvent.acceptTransferModes(COPY)
        }

        dragEvent.consume()
    }


    fun userDidDragDrop() = EventHandler<DragEvent> { dragEvent ->
        println("Drag drop: $dragEvent")

        FileOrImage(dragEvent)?.let {
            if (delegate.shouldAcceptDrop(it)) {
                state = holding(it)
                dragEvent.acceptTransferModes(COPY)
            }
            else {
                state = inactive
            }
        }

        dragEvent.consume()
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
//                is Image -> dropping(setOf(image(transferrable)))
//                is File -> dropping(setOf(file(transferrable)))
//                is List<*> -> dropping(
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
            class hovering(images: Set<FileOrImage>) : StateWithFiles(images, MaterialColors.lightBlue400, "Yeah that")

            /** When the user has just let go of the image(s), but they are not yet held by the drop target */
            class dropping(images: Set<FileOrImage>) : StateWithFiles(images, MaterialColors.lightBlue400, "Thanks! 👍🏽")

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
        class image(val image: Image): FileOrImage()

        val name: String get() = when (this) {
            is file -> this.file.name
            is image -> "🖼"
        }


        companion object {
            operator fun invoke(dragEvent: DragEvent): Set<FileOrImage>? {
                val dragboard = dragEvent.dragboard
                val all = mutableSetOf<FileOrImage>()

                val files = dragboard.files
                if (null != files) {
                    dragboard.files?.forEach { all.add(file(it)) }
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
         * Called to determine whether the drop target should accept or reject the given item
         */
        fun shouldAcceptDrop(items: Set<FileOrImage>): Boolean


        /**
         * Called after a drop was successfully completed
         */
        fun didReceiveDrop(items: Set<FileOrImage>): DropReaction
    }


    enum class DropReaction {
        processingNotStarted,
        processingSucceeded,
        processingFailed,
        ;
    }
}



private fun <T> Array<T>.containsAny(others: Iterable<T>): Boolean = this.intersect(others).isNotEmpty()
