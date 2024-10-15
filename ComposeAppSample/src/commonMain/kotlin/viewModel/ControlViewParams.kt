package viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.flow.MutableStateFlow

enum class FigureControlSizeOptions { MANUAL, AUTO }

object ControlViewParams {
    val fieldSize = MutableStateFlow(IntSize.Zero)
    var selectedOption = FigureControlSizeOptions.AUTO
    var cellSize = 20
    var fieldColor: Color? by mutableStateOf(null)
}