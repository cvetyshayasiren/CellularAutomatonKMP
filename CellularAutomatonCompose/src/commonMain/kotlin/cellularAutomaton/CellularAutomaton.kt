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
    val figureSize = state.figure.size.collectAsState()

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                width = with(localDensity) { coordinates.size.width.toDp() }
                height = with(localDensity) { coordinates.size.height.toDp() }
                basedOnWidth =
                    width / figureSize.value.width * figureSize.value.height < height
            },
        contentAlignment = Alignment.Center
    ) {
        val sizeModifier = if (basedOnWidth)
            Modifier.size(
                width = width,
                height = width / figureSize.value.width * figureSize.value.height
            ) else
            Modifier.size(
                width = height / figureSize.value.height * figureSize.value.width,
                height = height
            )
        _DrawCanvas(
            basedOnWidth,
            sizeModifier,
            state
        )
    }
}
@Composable
private fun _DrawCanvas(
    basedOnWidth: Boolean,
    sizeModifier: Modifier,
    state: CellularAutomatonState
) {
    val cells = state.cellState.collectAsState()
    val field = state.fieldState.collectAsState()
    val figure = state.figure
    val transitionColors = state.transitionColors.collectAsState()
    val generation = state.figure.generation.collectAsState()
    val figureSize = state.figure.size.collectAsState()

    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

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
                                val maxZoom = figureSize.value.width / 10f
                                zoom = (zoom * gestureZoom).coerceIn(1f, maxZoom)
                                offsetX =
                                    (offsetX + centroid.x / oldZoom) - (centroid.x / zoom + pan.x / oldZoom * zoom)
                                offsetY =
                                    (offsetY + centroid.y / oldZoom) - (centroid.y / zoom + pan.y / oldZoom * zoom)
                                offsetX = offsetX.coerceIn(0f, size.width - size.width / zoom)
                                offsetY = offsetY.coerceIn(0f, size.height - size.height / zoom)
                            }
                            if (zoom == 1f && field.value.isDrawable) {
                                val cellWidth = size.width.toFloat() / figureSize.value.width
                                val x = (centroid.x / cellWidth).toInt()
                                val y = (centroid.y / cellWidth).toInt()
                                if (x in 0 until figureSize.value.width
                                    && y in 0 until figureSize.value.height
                                ) {
                                    figure.addFigure(
                                        x = x,
                                        y = y,
                                        figure = CaFigure.One
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
                                val cellWidth = size.width.toFloat() / figureSize.value.width
                                figure.switchCell(
                                    x = (tapOffset.x / cellWidth).toInt(),
                                    y = (tapOffset.y / cellWidth).toInt()
                                )
                            }
                        }, onDoubleTap = { tapOffset ->
                            when (zoom) {
                                1f -> {
                                    if (field.value.isZoomable) {
                                        val maxZoom = figureSize.value.width / 10f
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
        val cellSizeOnCanvas = if(basedOnWidth) size.width / figureSize.value.width else
            size.height / figureSize.value.height
        val cornerRadiusOnCanvas = cellSizeOnCanvas * cells.value.cornerRadius * .4f
        val margins = cellSizeOnCanvas - cellSizeOnCanvas * cells.value.marginsRatio
        val halfMargins = margins / 2

        generation.value.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                if (cell > 0) {
                    drawRoundRect(
                        color = transitionColors.value
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
            repeat(figureSize.value.width + 1) {
                val x = cellSizeOnCanvas * it
                drawLine(
                    color = cells.value.color,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height)
                )
            }
            repeat(figureSize.value.height + 1) {
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