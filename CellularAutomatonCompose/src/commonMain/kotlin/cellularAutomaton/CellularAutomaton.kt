package cellularAutomaton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CellularAutomaton(
    figure: CellularAutomatonFigure = CARandomFigure(20, 20, .5f),
    rule: CellularAutomatonRule = CellularAutomatonRule(),
    drawState: CADrawState = CADrawState()
) {
    private val _figure: MutableStateFlow<CellularAutomatonFigure> = MutableStateFlow(figure)
    val figure: StateFlow<CellularAutomatonFigure> = _figure

    private val _rule: MutableStateFlow<CellularAutomatonRule> = MutableStateFlow(rule)
    val rule: StateFlow<CellularAutomatonRule> = _rule

    private val _drawState: MutableStateFlow<CADrawState> = MutableStateFlow(drawState)
    val drawState: StateFlow<CADrawState> = _drawState

    private var transitColors = MutableStateFlow(makeTransitionColors())

    private val _isRun: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRun: StateFlow<Boolean> = _isRun

    suspend fun setFigure(newFigure: CellularAutomatonFigure) {
        when(isRun.value) {
            true -> {
                stop()
                _figure.value = newFigure
                _isRun.value = true
            }
            false -> {
                _figure.value = newFigure
            }
        }
    }

    suspend fun setSize(width: Int? = null, height: Int? = null) {
        stop()
        setFigure(
            CARandomFigure(
                width = width ?: figure.value.width,
                height = height?: figure.value.height
            )
        )
    }

    fun setRule(newRule: CellularAutomatonRule) {
        _rule.value = newRule
        transitColors.value = makeTransitionColors()
    }

    fun setDrawStateParams(
        backgroundColor: Color? = null,
        primaryCellColor: Color? = null,
        secondaryCellColor: Color? = null,
        delay: Long? = null,
        cornerRadius: Float? = null,
        drawGrid: Boolean? = null,
        marginsRatio: Float? = null,
        isDrawable: Boolean? = null,
        isZoomable: Boolean? = null,
        padding: Dp? = null,
        shape: Shape? = null
    ) {
        val old = drawState.value
        _drawState.value = CADrawState(
            backgroundColor = backgroundColor?: old.backgroundColor,
            primaryCellColor = primaryCellColor?: old.primaryCellColor,
            secondaryCellColor = secondaryCellColor?: old.secondaryCellColor,
            delay = delay?: old.delay,
            cornerRadius = cornerRadius?: old.cornerRadius,
            drawGrid = drawGrid?: old.drawGrid,
            marginsRatio = marginsRatio?: old.marginsRatio,
            isDrawable = isDrawable?: old.isDrawable,
            isZoomable = isZoomable?: old.isZoomable,
            padding = padding?: old.padding,
            shape = shape?: old.shape
        )
        transitColors.value = makeTransitionColors()
    }

    private fun makeTransitionColors(): List<Color> {
        if (rule.value.aging == 0) {
            return listOf(drawState.value.primaryCellColor)
        }
        val rTransitList = transitionListOfFloat(
            drawState.value.primaryCellColor.red,
            drawState.value.secondaryCellColor.red,
            rule.value.aging
        )
        val gTransitList = transitionListOfFloat(
            drawState.value.primaryCellColor.green,
            drawState.value.secondaryCellColor.green,
            rule.value.aging
        )
        val bTransitList = transitionListOfFloat(
            drawState.value.primaryCellColor.blue,
            drawState.value.secondaryCellColor.blue,
            rule.value.aging
        )
        return List(rule.value.aging) {
            Color(
                rTransitList[it],
                gTransitList[it],
                bTransitList[it]
            )
        }
    }

    private fun transitionListOfFloat(from: Float, to: Float, parts: Int): List<Float> {
        return if (from < to) {
            val part = (to - from) / (parts - 1)
            List(parts - 1) { (from + it * part).coerceAtMost(255f) } + listOf(to)
        } else {
            val part = (from - to) / (parts - 1)
            List(parts - 1) { (from - it * part).coerceAtLeast(0f) } + listOf(to)
        }
    }

    suspend fun run() {
        if(_isRun.value) { return }
        _isRun.value = true
        while (_isRun.value) {
            _figure.value.nextStep(rule.value)
            delay(drawState.value.delay)
        }
    }

    suspend fun circularRun(behaviour: CARunBehaviour = CircularRunRandomBehaviour()) {
        if(_isRun.value) { return }
        var remainingCycles = behaviour.cycles ?: 0
        _isRun.value = true
        while (_isRun.value) {
            val oldGeneration = figure.value.generation.cellsState.value
            _figure.value.nextStep(rule.value)
            if(oldGeneration == figure.value.generation.cellsState.value) {
                remainingCycles = (remainingCycles - 1).coerceAtLeast(-1)
                when(remainingCycles) {
                    0 -> { stop() }
                    else -> { setFigure(behaviour.nextFigure()) }
                }
            }
            delay(drawState.value.delay)
        }
    }

    fun nextStep() {
        if(_isRun.value) { return }
        figure.value.nextStep(rule.value)
    }

    fun stop() {
        _isRun.value = false
    }

    @Composable
    fun Draw(
        size: DpSize? = null
    ) {
        val localDensity = LocalDensity.current
        var width by remember { mutableStateOf(1.dp) }
        var height by remember { mutableStateOf(1.dp) }
        var basedOnWidth by remember { mutableStateOf(true) }
        val state = drawState.collectAsState()
        val figure = figure.collectAsState()
        Box(
            Modifier
                .then(if(size != null) Modifier.size(size) else Modifier.fillMaxSize())
                .background(state.value.backgroundColor, shape = state.value.shape)
                .padding(state.value.padding)
                .onGloballyPositioned { coordinates ->
                    width = with(localDensity) { coordinates.size.width.toDp() }
                    height = with(localDensity) { coordinates.size.height.toDp() }
                    basedOnWidth =
                        width / _figure.value.width * _figure.value.height < height
                }, contentAlignment = Alignment.Center
        ) {
            val sizeModifier = if (basedOnWidth)
                Modifier.size(
                    width = width,
                    height = width / _figure.value.width * _figure.value.height
                ) else
                Modifier.size(
                    width = height / _figure.value.height * _figure.value.width,
                    height = height
                )
            _DrawCanvas(basedOnWidth, sizeModifier, figure.value, state)
        }
    }
    @Composable
    private fun _DrawCanvas(
        basedOnWidth: Boolean,
        sizeModifier: Modifier,
        actualFigure: CellularAutomatonFigure,
        state: State<CADrawState>
    ) {
        var zoom by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        val generation = actualFigure.generation.cellsState.collectAsState()

        Canvas(
            modifier = Modifier
                .then(sizeModifier)
                .clipToBounds()
                .then(if (state.value.isZoomable || state.value.isDrawable) Modifier
                    .graphicsLayer(
                        scaleX = zoom, scaleY = zoom,
                        translationX = -offsetX * zoom, translationY = -offsetY * zoom,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures(
                            onGesture = { centroid, pan, gestureZoom, _ ->
                                if (state.value.isZoomable) {
                                    val oldZoom = zoom
                                    val maxZoom = _figure.value.width / 10f
                                    zoom = (zoom * gestureZoom).coerceIn(1f, maxZoom)
                                    offsetX =
                                        (offsetX + centroid.x / oldZoom) - (centroid.x / zoom + pan.x / oldZoom * zoom)
                                    offsetY =
                                        (offsetY + centroid.y / oldZoom) - (centroid.y / zoom + pan.y / oldZoom * zoom)
                                    offsetX = offsetX.coerceIn(0f, size.width - size.width / zoom)
                                    offsetY = offsetY.coerceIn(0f, size.height - size.height / zoom)
                                }
                                if (zoom == 1f && state.value.isDrawable) {
                                    val cellWidth = size.width.toFloat() / _figure.value.width
                                    val x = (centroid.x / cellWidth).toInt()
                                    val y = (centroid.y / cellWidth).toInt()
                                    if (x in 0 until _figure.value.width
                                        && y in 0 until _figure.value.height
                                    ) {
                                        _figure.value.addFigure(
                                            x = x,
                                            y = y,
                                            figure = CABasicFigure(
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
                                if (state.value.isDrawable) {
                                    val cellWidth = size.width.toFloat() / _figure.value.width
                                    _figure.value.switchCell(
                                        x = (tapOffset.x / cellWidth).toInt(),
                                        y = (tapOffset.y / cellWidth).toInt()
                                    )
                                }
                            }, onDoubleTap = { tapOffset ->
                                when (zoom) {
                                    1f -> {
                                        if (state.value.isZoomable) {
                                            val maxZoom = _figure.value.width / 10f
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
            val cellSizeOnCanvas = if(basedOnWidth) size.width / _figure.value.width else
                size.height / _figure.value.height
            val cornerRadiusOnCanvas = cellSizeOnCanvas * state.value.cornerRadius * .4f
            val margins = cellSizeOnCanvas - cellSizeOnCanvas * state.value.marginsRatio
            val halfMargins = margins / 2
            generation.value.forEachIndexed { i, line ->
                line.forEachIndexed { j, cell ->
                    if (cell > 0) {
                        drawRoundRect(
                            color = transitColors.value
                                .getOrElse(cell - 1) { state.value.primaryCellColor },
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
            if (state.value.drawGrid) {
                repeat(_figure.value.width + 1) {
                    val x = cellSizeOnCanvas * it
                    drawLine(
                        color = state.value.primaryCellColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height)
                    )
                }
                repeat(_figure.value.height + 1) {
                    val y = cellSizeOnCanvas * it
                    drawLine(
                        color = state.value.primaryCellColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y)
                    )
                }
            }
        }
    }
}