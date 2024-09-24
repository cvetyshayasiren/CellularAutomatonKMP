import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cellularAutomaton.CaCellState
import cellularAutomaton.CaCycles
import cellularAutomaton.CaFieldState
import cellularAutomaton.CaFigure
import cellularAutomaton.CaRule
import cellularAutomaton.CaRunBehaviour
import cellularAutomaton.CaRunProperties
import cellularAutomaton.CellularAutomaton
import cellularAutomaton.CellularAutomatonState
import kotlin.system.measureTimeMillis

@Composable
fun SpeedTest(
    caState: CellularAutomatonState = CellularAutomatonState(
        figure = CaFigure.FromRandom(200, 200, .5f),
        rule = CaRule(),
        cellState = CaCellState(
            color = MaterialTheme.colorScheme.primary,
            agedColor = MaterialTheme.colorScheme.secondary,
            cornerRadius = .5f,
            marginsRatio = .9f
        ),
        fieldState = CaFieldState(
            isDrawGrid = false,
            isDrawable = false,
            isZoomable = false
        ),
        runProperties = CaRunProperties(
            delay = 0L,
            behaviour = CaRunBehaviour.RandomFigure(CaCycles.Infinite, 200, 200)
        )
    )
) {
    var time by remember { mutableLongStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CellularAutomaton(
            state = caState,
            modifier = Modifier.size(400.dp)
        )
        TextButton(
            onClick = {
                time = measureTimeMillis {
                    repeat(1_000) {
                        caState.nextStep()
                    }
                }
            }
        ) {
            Text("1 000 steps")
        }
        Text(text = "Время: $time ms.")
    }
}

//24.09 11:30 4005 3526 3527
