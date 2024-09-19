import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cellularAutomaton.CaFigure
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.BigSpacer
import theme.ControlCheckbox
import theme.ControlSection
import theme.ControlSlider
import theme.SmallSpacer
import theme.defaultPadding
import theme.defaultShape

@Composable
fun ControlView(caState: CellularAutomatonState) {
    val background = ModifierProperties.background.collectAsState()
    val padding = ModifierProperties.padding.collectAsState()
    val shapeCorner = ModifierProperties.shapeCorner.collectAsState()
    val cellState = caState.cellState.collectAsState()
    val fieldState = caState.fieldState.collectAsState()
    val runProperties = caState.runProperties.collectAsState()
    val figure = caState.figure.collectAsState()
    val rule = caState.rule.collectAsState()
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlSection(label = "Figure") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
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
                IconButton(onClick = { caState.nextStep() }) {
                    Text("\uD83E\uDDB6")
                }
                IconButton(onClick = { figure.value.randomiseState() }) {
                    Text("\uD83C\uDFB2")
                }
                IconButton(onClick = { figure.value.clearState() }) {
                    Text("\uD83D\uDDD1")
                }
            }

            ControlSlider(
                text = "Width ${figure.value.width}",
                value = figure.value.width.toFloat(),
                valueRange = (10f..200f)
            ) { caState.setSize(width = it.toInt()) }

            ControlSlider(
                text = "Height ${figure.value.height}",
                value = figure.value.height.toFloat(),
                valueRange = (10f..200f)
            ) { caState.setSize(height = it.toInt()) }

            FilePickerButton(rule.value.aging) { caState.setFigure(it) }
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
            RgbPicker(label = "background color", tint = background.value) {
                ModifierProperties.setBackground(it)
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

        TextButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    ModifierProperties.setBackground(randomColor())
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
}