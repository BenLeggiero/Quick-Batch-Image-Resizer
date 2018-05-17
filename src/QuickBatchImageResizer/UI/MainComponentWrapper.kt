@file:Suppress("PackageDirectoryMismatch")

package QuickBatchImageResizer

import LatteFX.*
import QuickBatchImageResizer.ImageDropTarget.*
import QuickBatchImageResizer.ImageDropTarget.DropReaction.*
import javafx.scene.layout.*

/**
 * @author Ben Leggiero
 * @since 2018-05-13
 */
class MainComponentWrapper : FXComponentWrapper(MainContent())


class MainContent: BorderPane() {
    init {
        top = TopBar(MainController)
        center = ImageDropTarget(MainController)
        bottom = BottomBar(MainController)
    }
}
