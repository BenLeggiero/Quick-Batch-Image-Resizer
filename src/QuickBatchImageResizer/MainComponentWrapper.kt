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


class MainContent: BorderPane(), ImageDropTarget.Delegate {
    init {
        top = TopBar()
        center = ImageDropTarget(this)
        bottom = BottomBar()
    }


    override fun shouldAcceptDrop(items: Set<FileOrImage>): Boolean {
        return true
    }


    override fun didReceiveDrop(items: Set<FileOrImage>): DropReaction {
        return processingNotStarted
    }
}


//class ImageOrFileDropTarget: Pane() {
//
//    private var state = DropTargetState.inactive
//
//    init {
//        onDragEntered = userDidStartDrag()
//        onDragOver = userDidContinueDrag()
//        onDragDropped = userDidDragDrop()
//
//        this.setMinSize(256.0, 256.0)
//
//        border = Border(BorderStroke(Paint.valueOf("#29B6F6"), BorderStrokeStyle.DASHED, CornerRadii(4.0), BorderWidths(4.0)))
//    }
//
//
//    fun userDidStartDrag() = EventHandler<DragEvent> {
//        println("Drag started: $it")
//        it.acceptTransferModes(COPY)
//    }
//
//
//    fun userDidContinueDrag() = EventHandler<DragEvent> {
//        println("Drag continued: $it")
//        it.acceptTransferModes(COPY)
//    }
//
//
//    fun userDidDragDrop() = EventHandler<DragEvent> {
//        println("Drag drop: $it")
//    }
//}



