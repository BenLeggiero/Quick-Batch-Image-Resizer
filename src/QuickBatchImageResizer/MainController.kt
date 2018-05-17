package QuickBatchImageResizer

import QuickBatchImageResizer.ImageDropTarget.*
import QuickBatchImageResizer.ImageDropTarget.DropReaction.*
import javafx.geometry.*

/**
 * @author Ben
 * @since 2018-05-16
 */
object MainController: TopBar.Delegate, ImageDropTarget.Delegate, BottomBar.Delegate {
    override var targetDimension = Dimension2D(640.0, 480.0)
    override var useRapidMode: Boolean = false
    override var overwriteExistingFiles: Boolean = false

    override fun didPressGoButton() {
        processFiles()
    }


    override fun shouldAcceptDrop(items: Set<FileOrImage>): Boolean {
        return true
    }


    override fun didReceiveDrop(items: Set<FileOrImage>): DropReaction {
        processFiles()
        return accepted
    }


    private fun processFiles() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}