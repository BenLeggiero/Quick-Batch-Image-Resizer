package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.*
import QuickBatchImageResizer.ImageDropTarget.DropReaction.*
import javafx.geometry.*
import java.io.*

/**
 * @author Ben
 * @since 2018-05-16
 */
object MainController: TopBar.Delegate, ImageDropTarget.Delegate, BottomBar.Delegate {
    override var targetDimension = Dimension2D(640.0, 480.0)
    override var useRapidMode: Boolean = false
    override var overwriteExistingFiles: Boolean = false

    var allImages = mutableSetOf<FileOrImage.image>()

    override fun didPressGoButton() {
        processFiles()
    }


    override fun shouldAcceptDrop(items: Set<FileOrImage>): Boolean {
        return true
    }


    override fun didReceiveDrop(items: Set<FileOrImage>): DropReaction {
        if (useRapidMode) {
            processFiles()
        }
        return accepted
    }


    private fun processFiles() {
        allImages.forEach {
            it.resized(newSize = targetDimension).write(it.path(uniqueness = "${targetDimension.width}x${targetDimension.height}"))
        }
        allImages = mutableSetOf()
    }


    private fun FileOrImage.image.path(uniqueness: String): File {
        if (null != originalFile) {
            if (overwriteExistingFiles) {
                return originalFile
            }
            else {
                return originalFile.madeUnique(hint = uniqueness)
            }
        }
    }
}

private fun File.madeUnique(hint: String): File {
    val firstFilenameIdea = "$nameWithoutExtension ($hint).$extension"
    val firstFileIdea = File("${this.parentFile.path}${File.separator}$firstFilenameIdea")

    if (firstFileIdea.exists()) {
        TODO
    }
    else {
        return firstFileIdea
    }
}
