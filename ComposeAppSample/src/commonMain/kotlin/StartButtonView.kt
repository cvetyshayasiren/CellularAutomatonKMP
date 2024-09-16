import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StartButtonView(caState: CellularAutomatonState, height: Dp) {
    val isRun = caState.isRun.collectAsState()
    val textColor = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error else
            MaterialTheme.colorScheme.onBackground
    )
    val colorFrom = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error.copy(alpha = .8f) else
            MaterialTheme.colorScheme.primary.copy(alpha = .8f)
    )
    val colorTo = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.onError.copy(alpha = .8f) else
            MaterialTheme.colorScheme.secondary.copy(alpha = .8f)
    )

    TextButton(
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorFrom.value,
                        colorTo.value
                    )
                )
            ),
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                when(isRun.value) {
                    true -> caState.stop()
                    false -> caState.run()
                }
            }
        }
    ) {
        Text(
            text = if(isRun.value) "🛑" else "▶\uFE0F",
            fontWeight = FontWeight.Bold,
            fontSize = (height/3).value.sp,
            color = textColor.value
        )
    }
}