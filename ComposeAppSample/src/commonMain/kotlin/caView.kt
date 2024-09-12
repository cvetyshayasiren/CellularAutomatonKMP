import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
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
import cellularAutomaton.CaCellState
import cellularAutomaton.CaFieldState
import cellularAutomaton.CaFigure
import cellularAutomaton.CaRule
import cellularAutomaton.CaRunProperties
import cellularAutomaton.CellularAutomatonState

@Composable
fun CaView(
    caState: CellularAutomatonState = CellularAutomatonState(
        figure = CaFigure.FromRandom(50, 50, .5f),
        rule = CaRule(),
        cellState = CaCellState(
            color = MaterialTheme.colorScheme.primary,
            agedColor = MaterialTheme.colorScheme.secondary,
            cornerRadius = .5f,
            marginsRatio = .9f
        ),
        fieldState = CaFieldState(
            isDrawGrid = false,
            isDrawable = true,
            isZoomable = true
        ),
        runProperties = CaRunProperties(
            delay = 100L
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
                HorizontalView(caState, size)
            }
            false -> {
                VerticalView(caState, size)
            }
        }
    }
}

fun randomColor() = Color((0..255).random(), (0..255).random(), (0..255).random())