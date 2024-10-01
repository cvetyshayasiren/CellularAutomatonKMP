import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.defaultColorShift
import theme.defaultShapeCorner

@Composable
fun StartButtonView(caState: CellularAutomatonState, height: Dp) {
    val figure = caState.figure.collectAsState()
    val isRun = caState.isRun.collectAsState()
    val textColor = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error else
            MaterialTheme.colorScheme.onBackground
    )
    val colorFrom = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.error.copy(alpha = .8f) else
            MaterialTheme.colorScheme.primary.copy(alpha = .8f)
    )
    val colorTo = animateColorAsState(
        if(isRun.value) MaterialTheme.colorScheme.onError.copy(alpha = .8f) else
            MaterialTheme.colorScheme.secondary.copy(alpha = .8f)
    )
    val brush = Brush.sweepGradient(
        colors = listOf(
            colorFrom.value,
            colorTo.value,
            colorFrom.value,
            )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(brush = brush),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(
            modifier = Modifier
                .fillMaxWidth(.5f)
                .fillMaxHeight(),
            shape =  RectangleShape,
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    when(isRun.value) {
                        true -> caState.stop()
                        false -> caState.run()
                    }
                }
            }
        ) {
            PlayButton(
                isRun = isRun.value,
                btnSize = height/3,
                btnColor = textColor.value
            )
        }

        IconButton(onClick = { caState.nextStep() }) {
            Text(text = "\uD83E\uDDB6")
        }
        IconButton(onClick = { figure.value.randomiseState() }) {
            Text("\uD83C\uDFB2")
        }
        IconButton(onClick = { figure.value.clearState() }) {
            Text("\uD83D\uDDD1")
        }
    }
}

@Composable
fun PlayButton(
    isRun: Boolean,
    btnSize: Dp,
    btnColor: Color
) {
    val radius = with(LocalDensity.current) { defaultShapeCorner.toPx() }
    AnimatedContent(
        targetState = isRun,
        modifier = Modifier.size(btnSize.coerceAtLeast(1.dp))
    ) {run ->
        when(run) {
            true -> {
                Canvas(modifier = Modifier) {
                    drawRoundRect(
                        color = btnColor,
                        cornerRadius = CornerRadius(radius, radius)
                    )
                }
            }
            false -> {
                Canvas(modifier = Modifier) {
                    val rect = Rect(Offset.Zero, size)
                    val trianglePath = Path().apply {
                        moveTo(rect.topLeft)
                        lineTo(rect.centerRight)
                        lineTo(rect.bottomLeft)
                        close()
                    }

                    drawIntoCanvas { canvas ->
                        canvas.drawOutline(
                            outline = Outline.Generic(trianglePath),
                            paint = Paint().apply {
                                color = btnColor
                                pathEffect = PathEffect.cornerPathEffect(radius)
                            }
                        )
                    }
                }
            }
        }
    }
}

fun Path.moveTo(offset: Offset) = moveTo(offset.x, offset.y)
fun Path.lineTo(offset: Offset) = lineTo(offset.x, offset.y)

fun Color.shift(fraction: Float = defaultColorShift): Color {
    return this.copy(
        alpha = this.alpha,
        red = (this.red + fraction).coerceIn(0f..1f),
        green = (this.green + fraction).coerceIn(0f..1f),
        blue = (this.blue + fraction).coerceIn(0f..1f),
    )
}