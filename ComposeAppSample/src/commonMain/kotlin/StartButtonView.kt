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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cellularAutomaton.CellularAutomaton
import cellularAutomaton.CircularRunRandomBehaviour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StartButtonView(ca: CellularAutomaton, height: Dp) {
    val isRun = ca.isRun.collectAsState()
    val textColor = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error else
            MaterialTheme.colorScheme.onBackground
    )
    val colorFrom = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error.copy(alpha = .5f) else
            MaterialTheme.colorScheme.primary.copy(alpha = .5f)
    )
    val colorTo = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.onError.copy(alpha = .5f) else
            MaterialTheme.colorScheme.secondary.copy(alpha = .5f)
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
                    true -> ca.stop()
                    false -> ca.circularRun(
                        CircularRunRandomBehaviour(
                            width = ca.figure.value.width,
                            height = ca.figure.value.height
                        )
                    )
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