@file:Suppress("PackageDirectoryMismatch")

package QuickBatchImageResizer

import LatteFX.*
import QuickBatchImageResizer.ImageDropTarget.State.StateWithFiles.*
import QuickBatchImageResizer.MainController.Delegate
import javafx.scene.layout.*

/**
 * @author Ben Leggiero
 * @since 2018-05-13
 */
class MainComponentWrapper : FXComponentWrapper(MainContent())


class MainContent: BorderPane(), Delegate {
    override fun preparingImagesForProcessing() {
        (center as? ImageDropTarget)?.state = processing(setOf())
    }

    override fun readyToProcessImages(images: Set<ImageDropTarget.FileOrImage>) {
        (center as? ImageDropTarget)?.state = holding(images)
    }

    override fun startedProcessingImages(images: Set<ImageDropTarget.FileOrImage>) {
        (center as? ImageDropTarget)?.state = processing(images)
    }

    override fun doneProcessingImages() {
        (center as? ImageDropTarget)?.clear()
    }

    init {
        top = TopBar(MainController)
        center = ImageDropTarget(MainController)
        bottom = BottomBar(MainController)

        MainController.delegate = this
    }
}
