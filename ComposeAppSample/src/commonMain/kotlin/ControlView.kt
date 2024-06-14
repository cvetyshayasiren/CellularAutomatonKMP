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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cellularAutomaton.CARandomFigure
import cellularAutomaton.CellularAutomaton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlView(ca: CellularAutomaton) {
    val drawState = ca.drawState.collectAsState()
    val figure = ca.figure.collectAsState()

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
                        ca.setFigure(
                            CARandomFigure(
                                width = (20..100).random(),
                                height = (20..100).random()
                            )
                        )
                    }
                }
            ) {
                Text("Случайный размер")
            }
            IconButton(onClick = { ca.nextStep() }) {
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
                CoroutineScope(Dispatchers.Default).launch { ca.setSize(width = it.toInt()) }
            },
            valueRange = (10f..200f)
        )
        Text("Высота ${figure.value.height}", Modifier.fillMaxWidth())
        Slider(
            value = figure.value.height.toFloat(),
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch { ca.setSize(height = it.toInt()) }
            },
            valueRange = (10f..200f)
        )
        Spacer(Modifier.size(40.dp))

        Text("Правило", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(20.dp))
        RuleControlView(ca)
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
            RgbPicker(drawState.value.backgroundColor) {
                ca.setDrawStateParams(backgroundColor = it)
            }
            RgbPicker(drawState.value.primaryCellColor) {
                ca.setDrawStateParams(primaryCellColor = it)
            }
            RgbPicker(drawState.value.secondaryCellColor) {
                ca.setDrawStateParams(secondaryCellColor = it)
            }
        }
        Spacer(Modifier.size(20.dp))
        Text("Задержка ${drawState.value.delay}ms", Modifier.fillMaxWidth())
        Slider(
            value = drawState.value.delay.toFloat(),
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    ca.setDrawStateParams(delay = it.toLong())
                }
            },
            valueRange = (0f..1000f)
        )
        Text("Углы ${(drawState.value.cornerRadius * 100).toInt()/100f}", Modifier.fillMaxWidth())
        Slider(
            value = drawState.value.cornerRadius,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    ca.setDrawStateParams(cornerRadius = it)
                }
            },
            valueRange = (0f..1f)
        )

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                ca.setDrawStateParams(drawGrid = !drawState.value.drawGrid)
            }
        ) {
            Text("Сетка")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = drawState.value.drawGrid,
                onCheckedChange = { ca.setDrawStateParams(drawGrid = it) }
            )
        }
        Text(
            text = "Отступы ${(drawState.value.marginsRatio * 100).toInt() / 100f}",
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = drawState.value.marginsRatio,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    ca.setDrawStateParams(marginsRatio = it)
                }
            },
            valueRange = (0f..2f)
        )
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                ca.setDrawStateParams(isDrawable = !drawState.value.isDrawable)
            }
        ) {
            Text("Рисовать")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = drawState.value.isDrawable,
                onCheckedChange = { ca.setDrawStateParams(isDrawable = it) }
            )
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                ca.setDrawStateParams(isZoomable = !drawState.value.isZoomable)
            }
        ) {
            Text("Жесты")
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = drawState.value.isZoomable,
                onCheckedChange = { ca.setDrawStateParams(isZoomable = it) }
            )
        }
        Text("Граница ${(drawState.value.padding.value * 100).toInt()/100}", Modifier.fillMaxWidth())
        Slider(
            value = drawState.value.padding.value,
            onValueChange = {
                CoroutineScope(Dispatchers.Default).launch {
                    ca.setDrawStateParams(padding = Dp(it) )
                }
            },
            valueRange = (0f..100f)
        )
        Text("Скругление", Modifier.fillMaxWidth())
        var shape by remember { mutableStateOf(0.dp) }
        Slider(
            value = shape.value,
            onValueChange = {
                shape = Dp(it)
                CoroutineScope(Dispatchers.Default).launch {
                    ca.setDrawStateParams(shape = RoundedCornerShape(shape))
                }
            },
            valueRange = (0f..500f)
        )
        TextButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    ca.setDrawStateParams(
                        backgroundColor = randomColor(),
                        primaryCellColor = randomColor(),
                        secondaryCellColor = randomColor(),
                        cornerRadius = (0..100).random()/100f,
                        drawGrid = listOf(true, false).random(),
                        marginsRatio = (0..200).random()/100f,
                    )
                }
            }
        ) {
            Text("СУПЕРРАНДОМ")
        }
    }
}