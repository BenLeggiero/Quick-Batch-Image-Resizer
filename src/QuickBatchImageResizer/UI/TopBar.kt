@file:Suppress("PackageDirectoryMismatch")

package QuickBatchImageResizer

import javafx.geometry.*
import javafx.scene.control.*
import javafx.scene.layout.*
import kotlin.properties.*


/**
 * @author Ben Leggiero
 * @since 2018-05-13
 */
class TopBar(initialDelegate: Delegate? = null): GridPane() {
    private val targetDimensionLabel = Label("Target Dimensions: ")
    private val targetDimensionXSpinner = Spinner<Int>(1, 99999, 640)
    private val targetDimensionSpinnerDivider = Label(" ✕ ")
    private val targetDimensionYSpinner = Spinner<Int>(1, 99999, 480)

    val delegate by Delegates.observable(initialDelegate) { _, _, newValue ->
        newValue?.targetDimension = targetDimension()
    }

    init {
        padding = Insets(4.0)
        hgap = 4.0
        vgap = 4.0

        setUpSpinner(targetDimensionXSpinner, ::didChangeXSpinner)
        setUpSpinner(targetDimensionYSpinner, ::didChangeYSpinner)

        add(targetDimensionLabel, 0, 0)
        add(targetDimensionXSpinner, 1, 0)
        add(targetDimensionSpinnerDivider, 2, 0)
        add(targetDimensionYSpinner, 3, 0)
    }

    private fun setUpSpinner(targetDimensionSpinner: Spinner<Int>, didChange: () -> Unit) {
        targetDimensionSpinner.prefWidth = 96.0
        targetDimensionSpinner.valueProperty().addListener { _ -> didChange() }
        targetDimensionSpinner.isEditable = true
    }


    private fun didChangeXSpinner() {
        delegate?.targetDimension = targetDimension()
    }


    private fun didChangeYSpinner() {
        delegate?.targetDimension = targetDimension()
    }


    private fun targetDimension()
            = Dimension2D(targetDimensionXSpinner.value.toDouble(), targetDimensionYSpinner.value.toDouble())



    interface Delegate {
        var targetDimension: Dimension2D
    }
}
