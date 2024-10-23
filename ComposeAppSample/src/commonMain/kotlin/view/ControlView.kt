package view

import viewModel.ModifierProperties
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cellularAutomaton.CaFigure
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import viewModel.ControlViewParams
import viewModel.FigureControlSizeOptions

@Composable
fun ControlView(caState: CellularAutomatonState) {
    val padding = ModifierProperties.padding.collectAsState()
    val shapeCorner = ModifierProperties.shapeCorner.collectAsState()
    val cellState = caState.cellState.collectAsState()
    val fieldState = caState.fieldState.collectAsState()
    val runProperties = caState.runProperties.collectAsState()

    LaunchedEffect(ControlViewParams.fieldSize) {
        if(ControlViewParams.selectedOption == FigureControlSizeOptions.AUTO) {
            caState.setFigure(
                CaFigure.FillRandomise(
                    canvasSize = ControlViewParams.fieldSize,
                    cellSize = ControlViewParams.cellSize
                )
            )
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlSection(label = "Figure") {
            FigureControlView(caState)
        }

        ControlSection(label = "Rule") {
            RuleControlView(caState)
        }

        ControlSection(label = "Cells") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RgbPicker(label = "cell color", tint = cellState.value.color) {
                    caState.setCellParams(color = it)
                }
                RgbPicker(label = "aged cell color", tint = cellState.value.agedColor) {
                    caState.setCellParams(agedColor = it)
                }
            }
            ControlSlider(
                text = "Corner radius ${(cellState.value.cornerRadius * 100).toInt()/100f}",
                value = cellState.value.cornerRadius,
                valueRange = (0f..1f)
            ) { caState.setCellParams(cornerRadius = it) }
            ControlSlider(
                text = "Margins ratio ${(cellState.value.marginsRatio * 100).toInt() / 100f}",
                value = cellState.value.marginsRatio,
                valueRange = (0f..2f)
            ) { caState.setCellParams(marginsRatio = it) }
        }

        ControlSection(label = "Field") {
            RgbPicker(
                label = "background color",
                tint = ControlViewParams.fieldColor
                    ?: MaterialTheme.colorScheme.surfaceVariant
            ) {
                ControlViewParams.fieldColor = it
            }
            ControlCheckbox(
                label = "Draw grid",
                checked = fieldState.value.isDrawGrid,
                onClick = {
                    caState.setFieldParams(isDrawGrid = !fieldState.value.isDrawGrid)
                },
                onCheckedChange =  { caState.setFieldParams(isDrawGrid = it) }
            )
            ControlCheckbox(
                label = "Drawable",
                checked = fieldState.value.isDrawable,
                onClick = {
                    caState.setFieldParams(isDrawable = !fieldState.value.isDrawable)
                },
                onCheckedChange = { caState.setFieldParams(isDrawable = it) }
            )
            ControlCheckbox(
                label = "Zoomable",
                checked = fieldState.value.isZoomable,
                onClick = {
                    caState.setFieldParams(isZoomable = !fieldState.value.isZoomable)
                },
                onCheckedChange = { caState.setFieldParams(isZoomable = it) }
            )
            ControlSlider(
                text = "Padding ${padding.value.value.toInt()}",
                value = padding.value.value,
                valueRange = (0f..100f)
            ) { ModifierProperties.setPadding(it.dp) }
            ControlSlider(
                text = "Shape",
                value = shapeCorner.value.value,
                valueRange = (0f..500f)
            ) { ModifierProperties.setShapeCorner(it.dp) }
        }

        ControlSection(label = "Run properties") {
            ControlSlider(
                text = "Delay ${runProperties.value.delay}ms",
                value = runProperties.value.delay.toFloat(),
                valueRange = (0f..1000f)
            ) { caState.setRunProperties(delay = it.toLong()) }
        }

        ControlSection(label = "Randomisation") {
            RandomisationView(caState)
        }
    }
}