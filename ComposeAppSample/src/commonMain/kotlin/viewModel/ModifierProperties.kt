package viewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import theme.defaultPadding
import theme.defaultShadow

object ModifierProperties {

    private val _padding: MutableStateFlow<Dp> = MutableStateFlow(10.dp)
    val padding: StateFlow<Dp> = _padding

    private val _shapeCorner: MutableStateFlow<Dp> = MutableStateFlow(10.dp)
    val shapeCorner: StateFlow<Dp> = _shapeCorner

    fun setPadding(padding: Dp) {
        _padding.value = padding
    }

    fun setShapeCorner(shapeCorner: Dp) {
        _shapeCorner.value = shapeCorner
    }
}

@Composable
fun caModifier(): Modifier {
    val padding = ModifierProperties.padding.collectAsState()
    val shapeCorner = ModifierProperties.shapeCorner.collectAsState()
    return Modifier
        .padding(defaultPadding)
        .shadow(defaultShadow, RoundedCornerShape(shapeCorner.value))
        .clip(RoundedCornerShape(shapeCorner.value))
        .background(ControlViewParams.fieldColor ?: MaterialTheme.colorScheme.surfaceVariant)
        .padding(padding.value)
        .onGloballyPositioned { coordinates -> ControlViewParams.fieldSize.value = coordinates.size }
}