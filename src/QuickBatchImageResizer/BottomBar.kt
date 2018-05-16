package QuickBatchImageResizer

import javafx.geometry.*
import javafx.geometry.HPos.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.layout.Priority.*
import kotlin.Double.*

/**
 * @author ben
 * @since 5/15/18.
 */
class BottomBar: GridPane() {
    private val overwriteExistingFilesCheckbox = CheckBox("Overwrite Existing Files")
    private val gap = Pane()
    private val rapidModeCheckbox = CheckBox("Rapid Mode")
    private val goButton = Button("Go")

    init {
        padding = Insets(4.0)
        hgap = 4.0
        vgap = 4.0

        rapidModeCheckbox.selectedProperty().addListener { _, _, newValue ->
            goButton.isDisable = newValue
        }

        add(overwriteExistingFilesCheckbox, 0, 0)
        add(gap, 1, 0)
        add(rapidModeCheckbox, 2, 0)
        add(goButton, 3, 0)

        this.columnConstraints.addAll(
            ColumnConstraints(),
            ColumnConstraints().apply { hgrow = ALWAYS; minWidth = 16.0 }
        )
    }
}
