package QuickBatchImageResizer

import LatteFX.*
import javafx.event.*
import javafx.scene.control.*
import javafx.scene.input.*
import javafx.scene.input.TransferMode.*
import javafx.scene.layout.*
import javafx.scene.paint.*

/**
 * @author Ben Leggiero
 * @since 2018-05-13
 */
class MainComponentWrapper() : FXComponentWrapper(MainContent()) {
}



class MainContent: BorderPane() {
    init {
        top = TopBar()
        center = ImageDropTarget()
    }
}



class TopBar: GridPane() {
    val targetDimensionLabel = Label("Target Dimensions: ")
    val targetDimensionXSpinner = Spinner<Int>(1, 99999, 640)
    val targetDimensionSpinnerDivider = Label(" ✕ ")
    val targetDimensionYSpinner = Spinner<Int>(1, 99999, 480)

    init {

        targetDimensionXSpinner.prefWidth = 96.0
        targetDimensionYSpinner.prefWidth = 96.0

        add(targetDimensionLabel, 0, 0)
        add(targetDimensionXSpinner, 1, 0)
        add(targetDimensionSpinnerDivider, 2, 0)
        add(targetDimensionYSpinner, 3, 0)
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



