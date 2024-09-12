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

@Composable
fun ControlView(caState: CellularAutomatonState) {
    var backgroundColor by remember { mutableStateOf(Color.White) }
    var padding by remember { mutableStateOf(0.dp) }
    var shape by remember { mutableStateOf(0.dp) }
    val cellState = caState.cellState.collectAsState()
    val fieldState = caState.fieldState.collectAsState()
    val runProperties = caState.runProperties.collectAsState()
    val figure = caState.figure.collectAsState()
    val rule = caState.rule.collectAsState()
    Column(
        Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.size(40.dp))

        Text("Фигура", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(20.dp))
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
                Text("Случайный размер")
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
        Text("Ширина ${figure.value.width}", Modifier.fillMaxWidth())
        Slider(
            value = figure.value.width.toFloat(),
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch { caState.setSize(width = it.toInt()) }
            },
            valueRange = (10f..200f)
        )
        Text("Высота ${figure.value.height}", Modifier.fillMaxWidth())
        Slider(
            value = figure.value.height.toFloat(),
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch { caState.setSize(height = it.toInt()) }
            },
            valueRange = (10f..200f)
        )
        FilePickerButton(rule.value.aging) { caState.setFigure(it) }
        Spacer(Modifier.size(40.dp))

        Text("Правило", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(20.dp))
        RuleControlView(caState)
        Spacer(Modifier.size(40.dp))

        Text("Прочее", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(20.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(5.dp))
            Text("Цвета")
            RgbPicker(backgroundColor) {
                backgroundColor = it
            }
            RgbPicker(cellState.value.color) {
                caState.setCellParams(color = it)
            }
            RgbPicker(cellState.value.agedColor) {
                caState.setCellParams(agedColor = it)
            }
        }
        Spacer(Modifier.size(20.dp))
        Text("Задержка ${runProperties.value.delay}ms", Modifier.fillMaxWidth())
        Slider(
            value = runProperties.value.delay.toFloat(),
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    caState.setRunProperties(delay = it.toLong())
                }
            },
            valueRange = (0f..1000f)
        )
        Text(
            text = "Углы ${(cellState.value.cornerRadius * 100).toInt()/100f}",
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = cellState.value.cornerRadius,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    caState.setCellParams(cornerRadius = it)
                }
            },
            valueRange = (0f..1f)
        )

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                caState.setFieldParams(isDrawGrid = !fieldState.value.isDrawGrid)
            }
        ) {
            Text("Сетка")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = fieldState.value.isDrawGrid,
                onCheckedChange = { caState.setFieldParams(isDrawGrid = it) }
            )
        }
        Text(
            text = "Отступы ${(cellState.value.marginsRatio * 100).toInt() / 100f}",
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = cellState.value.marginsRatio,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    caState.setCellParams(marginsRatio = it)
                }
            },
            valueRange = (0f..2f)
        )
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                caState.setFieldParams(isDrawable = !fieldState.value.isDrawable)
            }
        ) {
            Text("Рисовать")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = fieldState.value.isDrawable,
                onCheckedChange = { caState.setFieldParams(isDrawable = it) }
            )
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                caState.setFieldParams(isZoomable = !fieldState.value.isZoomable)
            }
        ) {
            Text("Жесты")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = fieldState.value.isZoomable,
                onCheckedChange = { caState.setFieldParams(isZoomable = it) }
            )
        }
        Text("Граница $padding", Modifier.fillMaxWidth())
        Slider(
            value = padding.value,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    padding = it.dp
                }
            },
            valueRange = (0f..100f)
        )
        Text("Скругление", Modifier.fillMaxWidth())

        Slider(
            value = shape.value,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    shape = it.dp
                }
            },
            valueRange = (0f..500f)
        )
        TextButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    backgroundColor = randomColor()
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
            Text("СУПЕРРАНДОМ")
        }
    }
}