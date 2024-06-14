import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cellularAutomaton.CADrawState
import cellularAutomaton.CARandomFigure
import cellularAutomaton.CellularAutomaton
import cellularAutomaton.CellularAutomatonRule

@Composable
fun CaView(
    ca: CellularAutomaton = CellularAutomaton(
        figure = CARandomFigure(50, 50, .5f),
        rule = CellularAutomatonRule(),
        drawState = CADrawState(
            backgroundColor = MaterialTheme.colorScheme.background,
            primaryCellColor = MaterialTheme.colorScheme.primary,
            secondaryCellColor = MaterialTheme.colorScheme.secondary,
            delay = 100L,
            cornerRadius = .5f,
            drawGrid = false,
            marginsRatio = .9f,
            isDrawable = true,
            isZoomable = true,
            padding = 10.dp,
            shape = RoundedCornerShape(10.dp)
        )
    )
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    AnimatedContent(
        targetState = size,
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
            .onGloballyPositioned {coordinates -> size = coordinates.size },
        contentAlignment = Alignment.Center
    ) {state ->
        when(state.width > state.height) {
            true -> {
                HorizontalView(ca, size)
            }
            false -> {
                VerticalView(ca, size)
            }
        }
    }
}

fun randomColor() = Color((0..255).random(), (0..255).random(), (0..255).random())