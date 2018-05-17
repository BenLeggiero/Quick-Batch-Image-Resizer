@file:Suppress("PackageDirectoryMismatch")

package QuickBatchImageResizer

import javafx.geometry.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.layout.Priority.*
import kotlin.properties.*


/**
 * @author Ben Leggiero
 * @since 2018-05-15
 */
class BottomBar(initialDelegate: Delegate? = null): GridPane() {
    private val overwriteExistingFilesCheckbox = CheckBox("Overwrite Existing Files")
    private val gap = Pane()
    private val rapidModeCheckbox = CheckBox("Auto")
    private val goButton = Button("Go")

    var delegate by Delegates.observable(initialDelegate) { _, _, newValue ->
        newValue?.overwriteExistingFiles = overwriteExistingFilesCheckbox.isSelected
        newValue?.useRapidMode = rapidModeCheckbox.isSelected
    }

    init {
        padding = Insets(4.0)
        hgap = 4.0
        vgap = 4.0

        goButton.isDefaultButton = true
        rapidModeCheckbox.selectedProperty().addListener { _ -> didPressRapidModeCheckbox() }
        overwriteExistingFilesCheckbox.selectedProperty().addListener { _ -> didPressOverwriteExistingFilesCheckbox() }
        goButton.setOnAction { didPressGoButton() }

        add(overwriteExistingFilesCheckbox, 0, 0)
        add(gap, 1, 0)
        add(rapidModeCheckbox, 2, 0)
        add(goButton, 3, 0)

        this.columnConstraints.addAll(
            ColumnConstraints(),
            ColumnConstraints().apply { hgrow = ALWAYS; minWidth = 16.0 }
        )
    }


    private fun didPressOverwriteExistingFilesCheckbox() {
        delegate?.overwriteExistingFiles = overwriteExistingFilesCheckbox.isSelected
    }


    private fun didPressRapidModeCheckbox() {
        val useRapidMode = rapidModeCheckbox.isSelected
        goButton.isDisable = useRapidMode
        delegate?.useRapidMode = useRapidMode
    }


    private fun didPressGoButton() {
        delegate?.didPressGoButton()
    }



    interface Delegate {
        var useRapidMode: Boolean
        var overwriteExistingFiles: Boolean
        fun didPressGoButton()
    }
}
