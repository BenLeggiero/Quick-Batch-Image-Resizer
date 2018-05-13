package QuickBatchImageResizer;


import java.awt.*
import java.awt.ComponentOrientation.*
import java.awt.GridBagConstraints.*
import java.awt.event.*
import java.beans.*
import java.io.*
import javax.swing.*
import javax.swing.Action.*
import javax.swing.JCheckBox
import javax.swing.JSpinner
import javax.swing.event.*
import kotlin.properties.*


/**
 * @author ben
 * @since 5/12/18.
 */
class MainWindow: JFrame("Quick Batch Image Resizer") {

    private var targetDimensionsLabel = JLabel("Target Dimensions: ")
    private var widthSpinner = JSpinner(DimensionSpinnerModel(640))
    private var dimensionDelimiterLabel = JLabel("✕")
    private var heightSpinner = JSpinner(DimensionSpinnerModel(480))

    private var spacerBetweenDimensionsAndRapidMode = JPanel()

    private var rapidModeCheckBox = JCheckBox("Rapid Mode")
    private var outputDirLabel = JLabel("")
    private var outputDirChooseButton = JButton(ChooseOutputDirAction())

    private var fileDropTarget = ImageDropTarget()

    private var outputDir by Delegates.observable(null as File?) { _, _, newValue ->
        outputDirLabel.text = newValue?.path
    }

    init {
        rapidModeCheckBox.componentOrientation = RIGHT_TO_LEFT

        fun layOutUi() {
            /*

            +--------------------+----------------+---+-----------------+---------+---------------+
            | Target Dimensions: | [widthSpinner] | ✕ | [heightSpinner] | - - - - | Rapid Mode ☑️ |
            +--------------------+----------------+---+-----------------+---------+---------------+
            |                                /Path/To/Chosen/Output/Dir | [outputDirChooseButton] |
            +-----------------------------------------------------------+-------------------------+
            |                                                                                     |
            |                                   [fileDropTarget]                                  |
            |                                                                                     |
            +-------------------------------------------------------------------------------------+

             */

            this.layout = GridBagLayout()


            val gridBagConstraints = GridBagConstraints(
                    0,0,
                    1,1,
                    0.0,0.0,
                    LINE_START,
                    NONE,
                    Insets(4,4,4,4),
                    0,0
            )

            gridBagConstraints.anchor = LINE_END
            add(targetDimensionsLabel, gridBagConstraints)

            gridBagConstraints.gridx += 1
            add(widthSpinner, gridBagConstraints)

            gridBagConstraints.gridx += 1
            gridBagConstraints.anchor = CENTER
            add(dimensionDelimiterLabel, gridBagConstraints)

            gridBagConstraints.gridx += 1
            gridBagConstraints.anchor = LINE_START
            add(heightSpinner, gridBagConstraints)

            gridBagConstraints.gridx += 1
            gridBagConstraints.weightx = 1.0
            add(spacerBetweenDimensionsAndRapidMode, gridBagConstraints)

            gridBagConstraints.gridx += 1
            gridBagConstraints.weightx = 0.0
            gridBagConstraints.anchor = LINE_END
            add(rapidModeCheckBox, gridBagConstraints)


            gridBagConstraints.gridx = 0
            gridBagConstraints.gridy += 1
            gridBagConstraints.gridwidth = 4
            add(outputDirLabel, gridBagConstraints)

            gridBagConstraints.gridx = 4
            gridBagConstraints.gridwidth = 2
            gridBagConstraints.anchor = CENTER
            add(outputDirChooseButton, gridBagConstraints)


            gridBagConstraints.gridx = 0
            gridBagConstraints.gridy += 1
            gridBagConstraints.gridwidth = REMAINDER
            gridBagConstraints.weightx = 1.0
            gridBagConstraints.weighty = 1.0
            add(fileDropTarget, gridBagConstraints)
        }

        layOutUi()
    }



    inner class ChooseOutputDirAction: Action {

        val fileChooser: JFileChooser by lazy { JFileChooser() }

        private var _enabled: Boolean = true
        var arbitraryValues = mutableMapOf<String, Any>(
                NAME to "Choose an output directory"
        )
        private var propertyChangeListeners = mutableSetOf<PropertyChangeListener>()

        override fun isEnabled(): Boolean {
            return _enabled
        }

        override fun addPropertyChangeListener(listener: PropertyChangeListener?) {
            propertyChangeListeners.add(listener ?: return)
        }

        override fun setEnabled(b: Boolean) {
            _enabled = b
        }

        override fun actionPerformed(e: ActionEvent?) {
            val chosenOption = fileChooser.showOpenDialog(this@MainWindow)

            when (chosenOption) {
                JFileChooser.APPROVE_OPTION -> outputDir = fileChooser.selectedFile
            }
        }

        override fun getValue(key: String?) = arbitraryValues[key]

        override fun putValue(key: String, value: Any?) {
            if (null != value) {
                arbitraryValues[key] = value
            }
            else {
                arbitraryValues.remove(key)
            }
        }

        override fun removePropertyChangeListener(listener: PropertyChangeListener?) {
            propertyChangeListeners.remove(listener)
        }
    }
}



class DimensionSpinnerModel(initialValue: Int): SpinnerModel {

    var value: Int by Delegates.observable(initialValue) { _, _, _ ->
        val changeEvent = ChangeEvent(null)
        changeListeners.forEach { it(changeEvent) }
    }

    var changeListeners = mutableSetOf<ChangeListener>()

    override fun setValue(value: Any?) {
        this.value = value.toInt() ?: 0
    }

    override fun getNextValue() = when (value) {
        Int.MAX_VALUE -> null
        else -> value + 1
    }

    override fun removeChangeListener(l: ChangeListener?) {
        changeListeners.remove(l)
    }

    override fun addChangeListener(l: ChangeListener?) {
        changeListeners.add(l ?: return)
    }

    override fun getValue() = value

    override fun getPreviousValue() = value - 1
}



fun Any?.toInt() = when (this) {
    null -> null
    is Int -> this
    is Number -> this.toInt()
    is String -> this.toIntOrNull()
    else -> null
}



operator fun ChangeListener.invoke(changeEvent: ChangeEvent) {
    this.stateChanged(changeEvent)
}
