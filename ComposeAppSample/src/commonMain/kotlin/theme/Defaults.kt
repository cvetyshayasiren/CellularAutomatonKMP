package theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultPadding = 8.dp
val defaultShapeCorner = 12.dp
val defaultShape = RoundedCornerShape(defaultShapeCorner)
val defaultShadow = 4.dp

@Composable
fun BigSpacer() {
    Spacer(Modifier.size(defaultPadding * 4))
}

@Composable
fun SmallSpacer() {
    Spacer(Modifier.size(defaultPadding * 2))
}

@Composable
fun ControlSlider(
    text: String,
    value: Float,
    valueRange:  ClosedFloatingPointRange<Float>,
    onValueChange: suspend (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = defaultPadding)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .05f))
            .padding(defaultPadding / 2),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
        Slider(
            value = value,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch { onValueChange(it) }
            },
            valueRange = valueRange
        )
    }
}

@Composable
fun ControlCheckbox(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(label)
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ControlSection(label: String, content: @Composable() (ColumnScope.() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BigSpacer()
        Text(label, fontWeight = FontWeight.Bold)
        SmallSpacer()
        content()
        SmallSpacer()
        HorizontalDivider()
    }
}
