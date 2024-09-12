package cellularAutomaton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
        _DrawCanvas(basedOnWidth, sizeModifier, figure.value, transitionColors.value, cell, field)
    }
}
@Composable
private fun _DrawCanvas(
    basedOnWidth: Boolean,
    sizeModifier: Modifier,
    actualFigure: CaFigure,
    transitionColors: List<Color>,
    cells: State<CaCellState>,
    field: State<CaFieldState>
) {
    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val generation = actualFigure.generation.cellsState.collectAsState()

    Canvas(
        modifier = Modifier
            .then(sizeModifier)
            .clipToBounds()
            .then(if (field.value.isZoomable || field.value.isDrawable) Modifier
                .graphicsLayer(
                    scaleX = zoom, scaleY = zoom,
                    translationX = -offsetX * zoom, translationY = -offsetY * zoom,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { centroid, pan, gestureZoom, _ ->
                            if (field.value.isZoomable) {
                                val oldZoom = zoom
                                val maxZoom = actualFigure.width / 10f
                                zoom = (zoom * gestureZoom).coerceIn(1f, maxZoom)
                                offsetX =
                                    (offsetX + centroid.x / oldZoom) - (centroid.x / zoom + pan.x / oldZoom * zoom)
                                offsetY =
                                    (offsetY + centroid.y / oldZoom) - (centroid.y / zoom + pan.y / oldZoom * zoom)
                                offsetX = offsetX.coerceIn(0f, size.width - size.width / zoom)
                                offsetY = offsetY.coerceIn(0f, size.height - size.height / zoom)
                            }
                            if (zoom == 1f && field.value.isDrawable) {
                                val cellWidth = size.width.toFloat() / actualFigure.width
                                val x = (centroid.x / cellWidth).toInt()
                                val y = (centroid.y / cellWidth).toInt()
                                if (x in 0 until actualFigure.width
                                    && y in 0 until actualFigure.height
                                ) {
                                    actualFigure.addFigure(
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
                            if (field.value.isDrawable) {
                                val cellWidth = size.width.toFloat() / actualFigure.width
                                actualFigure.switchCell(
                                    x = (tapOffset.x / cellWidth).toInt(),
                                    y = (tapOffset.y / cellWidth).toInt()
                                )
                            }
                        }, onDoubleTap = { tapOffset ->
                            when (zoom) {
                                1f -> {
                                    if (field.value.isZoomable) {
                                        val maxZoom = actualFigure.width / 10f
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
        val cellSizeOnCanvas = if(basedOnWidth) size.width / actualFigure.width else
            size.height / actualFigure.height
        val cornerRadiusOnCanvas = cellSizeOnCanvas * cells.value.cornerRadius * .4f
        val margins = cellSizeOnCanvas - cellSizeOnCanvas * cells.value.marginsRatio
        val halfMargins = margins / 2
        generation.value.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                if (cell > 0) {
                    drawRoundRect(
                        color = transitionColors
                            .getOrElse(cell - 1) { cells.value.color },
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
        if (field.value.isDrawGrid) {
            repeat(actualFigure.width + 1) {
                val x = cellSizeOnCanvas * it
                drawLine(
                    color = cells.value.color,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height)
                )
            }
            repeat(actualFigure.height + 1) {
                val y = cellSizeOnCanvas * it
                drawLine(
                    color = cells.value.color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y)
                )
            }
        }
    }
}