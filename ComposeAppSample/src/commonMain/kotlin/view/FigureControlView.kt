package view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cellularAutomaton.CaFigure
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.defaultPadding
import viewModel.ControlViewParams
import viewModel.FigureControlSizeOptions

@Composable
fun FigureControlView(
    caState: CellularAutomatonState
) {
    val figureSize = caState.figure.size.collectAsState()
    val rule = caState.rule.collectAsState()

    Text("Size ${figureSize.value.width} x ${figureSize.value.height}")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FigureControlSizeOptions.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(option.name.lowercase())
                RadioButton(
                    selected = option == ControlViewParams.selectedOption,
                    onClick = {
                        ControlViewParams.selectedOption = option
                    }
                )
            }
        }
    }

    AnimatedContent(targetState = ControlViewParams.selectedOption, label = "AnimateOptions") {option ->
        when(option) {
            FigureControlSizeOptions.MANUAL -> {
                Column  {
                    ControlSlider(
                        text = "Width ${figureSize.value.width}",
                        value = figureSize.value.width.toFloat(),
                        valueRange = (10f..200f)
                    ) { caState.setSize(width = it.toInt()) }

                    ControlSlider(
                        text = "Height ${figureSize.value.height}",
                        value = figureSize.value.height.toFloat(),
                        valueRange = (10f..200f)
                    ) { caState.setSize(height = it.toInt()) }
                }
            }
            FigureControlSizeOptions.AUTO -> {
                ControlSlider(
                    text = "cellSize ${ControlViewParams.cellSize}",
                    value = ControlViewParams.cellSize.toFloat(),
                    valueRange = (5f..100f)
                ) {
                    ControlViewParams.cellSize = it.toInt()
                    caState.setFigure(
                        CaFigure.FillRandomise(
                            canvasSize = ControlViewParams.fieldSize,
                            cellSize = ControlViewParams.cellSize
                        )
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(defaultPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("add")
        (1..listOf(figureSize.value.width, figureSize.value.height).min()).forEach {
            TextButton(
                onClick = {
                    caState.figure.addFigureInCenter(
                        CaFigure.Rectangle(it,it)
                    )
                }
            ) {
                Text("${it}x$it")
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = {
                ControlViewParams.selectedOption = FigureControlSizeOptions.MANUAL
                CoroutineScope(Dispatchers.IO).launch {
                    caState.setFigure(
                        CaFigure.FromRandom(
                            width = (20..100).random(),
                            height = (20..100).random()
                        )
                    )
                }
            }
        ) {
            Text("Random size")
        }
        FilePickerButton(rule.value.aging) { caState.setFigure(it) }
    }
}