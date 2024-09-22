package cellularAutomaton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
@Composable
fun CellularAutomaton(
    state: CellularAutomatonState,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current
    var width by remember { mutableStateOf(1.dp) }
    var height by remember { mutableStateOf(1.dp) }
    var basedOnWidth by remember { mutableStateOf(true) }
    val cell = state.cellState.collectAsState()
    val field = state.fieldState.collectAsState()
    val figure = state.figure.collectAsState()
    val transitionColors = state.transitionColors.collectAsState()
    val generation = figure.value.generation.cellsState.collectAsState()

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                width = with(localDensity) { coordinates.size.width.toDp() }
                height = with(localDensity) { coordinates.size.height.toDp() }
                basedOnWidth =
                    width / figure.value.width * figure.value.height < height
            },
        contentAlignment = Alignment.Center
    ) {
        val sizeModifier = if (basedOnWidth)
            Modifier.size(
                width = width,
                height = width / figure.value.width * figure.value.height
            ) else
            Modifier.size(
                width = height / figure.value.height * figure.value.width,
                height = height
            )
        _DrawCanvas(
            basedOnWidth,
            sizeModifier,
            figure.value,
            transitionColors.value,
            cell.value,
            field.value,
            generation.value
        )
    }
}
@Composable
private fun _DrawCanvas(
    basedOnWidth: Boolean,
    sizeModifier: Modifier,
    figure: CaFigure,
    transitionColors: List<Color>,
    cells: CaCellState,
    field: CaFieldState,
    generation: List<List<Int>>
) {
    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Canvas(
        modifier = Modifier
            .then(sizeModifier)
            .clipToBounds()
            .then(if (field.isZoomable || field.isDrawable) Modifier
                .graphicsLayer(
                    scaleX = zoom, scaleY = zoom,
                    translationX = -offsetX * zoom, translationY = -offsetY * zoom,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { centroid, pan, gestureZoom, _ ->
                            if (field.isZoomable) {
                                val oldZoom = zoom
                                val maxZoom = figure.width / 10f
                                zoom = (zoom * gestureZoom).coerceIn(1f, maxZoom)
                                offsetX =
                                    (offsetX + centroid.x / oldZoom) - (centroid.x / zoom + pan.x / oldZoom * zoom)
                                offsetY =
                                    (offsetY + centroid.y / oldZoom) - (centroid.y / zoom + pan.y / oldZoom * zoom)
                                offsetX = offsetX.coerceIn(0f, size.width - size.width / zoom)
                                offsetY = offsetY.coerceIn(0f, size.height - size.height / zoom)
                            }
                            if (zoom == 1f && field.isDrawable) {
                                val cellWidth = size.width.toFloat() / figure.width
                                val x = (centroid.x / cellWidth).toInt()
                                val y = (centroid.y / cellWidth).toInt()
                                if (x in 0 until figure.width
                                    && y in 0 until figure.height
                                ) {
                                    figure.addFigure(
                                        x = x,
                                        y = y,
                                        figure = CaFigure.FromGeneration(
                                            CAGeneration(listOf(listOf(1)))
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            if (field.isDrawable) {
                                val cellWidth = size.width.toFloat() / figure.width
                                figure.switchCell(
                                    x = (tapOffset.x / cellWidth).toInt(),
                                    y = (tapOffset.y / cellWidth).toInt()
                                )
                            }
                        }, onDoubleTap = { tapOffset ->
                            when (zoom) {
                                1f -> {
                                    if (field.isZoomable) {
                                        val maxZoom = figure.width / 10f
                                        zoom = maxZoom / 2
                                        offsetX = (tapOffset.x * zoom).coerceIn(
                                            0f,
                                            size.width - size.width / zoom
                                        )
                                        offsetY = (tapOffset.y * zoom).coerceIn(
                                            0f,
                                            size.height - size.height / zoom
                                        )
                                    }
                                }
                                else -> {
                                    zoom = 1f; offsetX = 0f; offsetY = 0f
                                }
                            }
                        }
                    )
                }
            else Modifier)
    ) {
        val cellSizeOnCanvas = if(basedOnWidth) size.width / figure.width else
            size.height / figure.height
        val cornerRadiusOnCanvas = cellSizeOnCanvas * cells.cornerRadius * .4f
        val margins = cellSizeOnCanvas - cellSizeOnCanvas * cells.marginsRatio
        val halfMargins = margins / 2

        generation.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                if (cell > 0) {
                    drawRoundRect(
                        color = transitionColors
                            .getOrElse(cell - 1) { cells.color },
                        topLeft = Offset(
                            x = j * cellSizeOnCanvas + halfMargins,
                            y = i * cellSizeOnCanvas + halfMargins
                        ),
                        size = Size(
                            width = cellSizeOnCanvas - margins,
                            height = cellSizeOnCanvas - margins
                        ),
                        cornerRadius = CornerRadius(x = cornerRadiusOnCanvas)
                    )
                }
            }
        }
        if (field.isDrawGrid) {
            repeat(figure.width + 1) {
                val x = cellSizeOnCanvas * it
                drawLine(
                    color = cells.color,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height)
                )
            }
            repeat(figure.height + 1) {
                val y = cellSizeOnCanvas * it
                drawLine(
                    color = cells.color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y)
                )
            }
        }
    }
}