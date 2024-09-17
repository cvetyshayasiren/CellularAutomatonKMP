import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cellularAutomaton.CaFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ModifierProperties {

    private val _background: MutableStateFlow<Color> = MutableStateFlow(Color.Gray)
    val background: StateFlow<Color> = _background

    private val _padding: MutableStateFlow<Dp> = MutableStateFlow(10.dp)
    val padding: StateFlow<Dp> = _padding

    private val _shapeCorner: MutableStateFlow<Dp> = MutableStateFlow(10.dp)
    val shapeCorner: StateFlow<Dp> = _shapeCorner

    fun setBackground(color: Color) {
        _background.value = color
    }

    fun setPadding(padding: Dp) {
        _padding.value = padding
    }

    fun setShapeCorner(shapeCorner: Dp) {
        _shapeCorner.value = shapeCorner
    }
}

@Composable
fun caModifier(): Modifier {
    val background = ModifierProperties.background.collectAsState()
    val padding = ModifierProperties.padding.collectAsState()
    val shapeCorner = ModifierProperties.shapeCorner.collectAsState()
    return Modifier
        .padding(10.dp)
        .shadow(10.dp, RoundedCornerShape(shapeCorner.value))
        .clip(RoundedCornerShape(shapeCorner.value))
        .background(background.value)
        .padding(padding.value)
}