package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.FileOrImage.*
import QuickBatchImageResizer.ImageDropTarget.State.*
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.*
import java.awt.*
import java.awt.datatransfer.DataFlavor.*
import java.io.*
import javax.swing.*

/**
 * @author Ben Leggiero
 * @since 2018-05-12
 */
class ImageDropTarget: JComponent() {

    var state: State = inactive

    init {
//        dropTarget.addDropTargetListener(this)
        this.transferHandler = FileTransferHandler()
        dropTarget.isActive = true
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



    override fun paint(g: Graphics?) {
        if (null == g) {
            println("Null graphics in paint method...?")
            return
        }
        val state = this.state

        g.clearRect(0, 0, width, height)

        val text = when (state) {
            is StateWithFiles -> state.filesString
            is denying -> "🚫 Not Supported"
            else -> "Drop file(s) here"
        }

        g.font = Font.getFont(Font.DIALOG).deriveFont(24.0f)
        g.drawString(text, 10, 10)
    }


//    override fun dropActionChanged(dtde: DropTargetDragEvent?) {
//        this.state =
//    }
//
//    override fun drop(dtde: DropTargetDropEvent?) {
//        this.state = State(dtde ?: return)
//    }
//
//    override fun dragOver(dtde: DropTargetDragEvent?) {
//        this.state = State(dtde ?: return)
//    }
//
//    override fun dragExit(dte: DropTargetEvent?) {
//        this.state = State(dte ?: return)
//    }
//
//    override fun dragEnter(dtde: DropTargetDragEvent?) {
//        this.state = State(dtde ?: return)
//    }



    sealed class State {
        object inactive: State()
        object denying: State()

        sealed class StateWithFiles(val fileOrImages: Set<FileOrImage>): State() {
            class hovering(files: Set<FileOrImage>) : StateWithFiles(files)
            class dropping(files: Set<FileOrImage>) : StateWithFiles(files)
            class holding(files: Set<FileOrImage>) : StateWithFiles(files)

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
}

private fun <T> Array<T>.containsAny(others: Iterable<T>): Boolean = this.intersect(others).isNotEmpty()
