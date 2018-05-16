package QuickBatchImageResizer

import javafx.geometry.*
import javafx.scene.control.*
import javafx.scene.layout.*


/**
 * @author Ben Leggiero
 * @since 2018-05-13
 */
class TopBar: GridPane() {
    private val targetDimensionLabel = Label("Target Dimensions: ")
    private val targetDimensionXSpinner = Spinner<Int>(1, 99999, 640)
    private val targetDimensionSpinnerDivider = Label(" ✕ ")
    private val targetDimensionYSpinner = Spinner<Int>(1, 99999, 480)

    init {
        padding = Insets(4.0)
        hgap = 4.0
        vgap = 4.0

        targetDimensionXSpinner.prefWidth = 96.0
        targetDimensionYSpinner.prefWidth = 96.0

        add(targetDimensionLabel, 0, 0)
        add(targetDimensionXSpinner, 1, 0)
        add(targetDimensionSpinnerDivider, 2, 0)
        add(targetDimensionYSpinner, 3, 0)
    }
}