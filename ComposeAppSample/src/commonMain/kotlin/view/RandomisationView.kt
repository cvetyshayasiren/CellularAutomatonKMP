package view

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import viewModel.ControlViewParams
import viewModel.ModifierProperties

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RandomisationView(caState: CellularAutomatonState) {
    FlowRow {

    }
    TextButton(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                ControlViewParams.fieldColor = randomColor()
                ModifierProperties.setPadding((0..100).random().dp)
                ModifierProperties.setShapeCorner((0..500).random().dp)
                caState.setCellParams(
                    color = randomColor(),
                    agedColor = randomColor(),
                    cornerRadius = (0..100).random()/100f,
                    marginsRatio = (0..200).random()/100f
                )
                caState.setFieldParams(
                    isDrawGrid = listOf(true, false).random()
                )
            }
        }
    ) {
        Text("Super random")
    }
}