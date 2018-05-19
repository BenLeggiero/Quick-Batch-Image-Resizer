package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.*
import QuickBatchImageResizer.ImageDropTarget.DropReaction.*
import QuickBatchImageResizer.ImageDropTarget.FileOrImage.*
import javafx.embed.swing.*
import javafx.geometry.*
import javafx.scene.image.*
import java.io.*

/**
 * @author Ben
 * @since 2018-05-16
 */
object MainController: TopBar.Delegate, ImageDropTarget.Delegate, BottomBar.Delegate {
    override var targetDimension = Dimension2D(640.0, 480.0)
    override var useRapidMode: Boolean = false
    override var overwriteExistingFiles: Boolean = false

    var delegate: Delegate? = null

    var allImages = mutableSetOf<FileOrImage.image>()

    override fun didPressGoButton() {
        processImages()
    }


    override fun shouldAcceptDrop(items: Set<FileOrImage>): Boolean {
        return true
    }


    override fun didReceiveDrop(items: Set<FileOrImage>): DropReaction {
        allImages.addAll(items.mapToImages())

        if (useRapidMode) {
            processImages()
        }
        return accepted
    }


    private fun processImages() {
        allImages.forEach {
            it.resized(newSize = targetDimension).write(it.outputFile(uniqueness = "${targetDimension.width}x${targetDimension.height}",
                                                                      format = it.format))
        }
        allImages = mutableSetOf()

        delegate?.doneProcessingImages()
    }


    private fun FileOrImage.image.outputFile(uniqueness: String, format: String): File {
        return if (null != originalFile) {
            if (overwriteExistingFiles) {
                originalFile
            }
            else {
                originalFile.madeUnique(hint = uniqueness)
            }
        }
        else {
            File("Untitled.$format").madeUnique(uniqueness)
        }
    }


    interface Delegate {
        fun doneProcessingImages()
    }
}

private fun Set<FileOrImage>.mapToImages(): Set<image> {
    return map {
        when (it) {
            is image -> it
            is file -> image(Image(it.file.inputStream()))
        }
    }.toSet()
}

private val FileOrImage.format: String
    get() = when (this) {
        is file -> file.extension
        is image -> originalFile?.extension ?: "png"
    }


private fun File.madeUnique(hint: String): File {
    val firstFilenameIdea = "$nameWithoutExtension ($hint).$extension"
    val firstFileIdea = File("${this.parentFile.path}${File.separator}$firstFilenameIdea")

    if (firstFileIdea.exists()) {
        var filenameIdea: String
        var fileIdea: File
        var counter = 1

        do {
            filenameIdea = "$nameWithoutExtension ($hint $counter).$extension"
            fileIdea = File("${this.parentFile.path}${File.separator}$filenameIdea")
            counter += 1
        } while (fileIdea.exists() && counter < Int.MAX_VALUE)

        return fileIdea
    }
    else {
        return firstFileIdea
    }
}
