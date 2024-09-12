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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CACell(
    val cellColor: Color = Color(96, 108, 56),
    val agedCellColor: Color = Color(221, 161, 94),
    val cellCornerRadius: Float = .5f,
    val marginsRatio: Float = .9f,
)

data class CAField(
    val isDrawGrid: Boolean = false,
    val isDrawable: Boolean = false,
    val isZoomable: Boolean = false
)

data class CARunProperties(
    val delay: Long = 100,
    val behaviour: CARunBehaviour = CARunBehaviour.Simple
)

@Composable
fun rememberCellularAutomatoState(): CellularAutomatoState {
    return remember { CellularAutomatoState() }
}

class CellularAutomatoState(
    figure: CellularAutomatonFigure = CARandomFigure(20, 20, .5f),
    rule: CellularAutomatonRule = CellularAutomatonRule(),
    cell: CACell = CACell(),
    field: CAField = CAField(),
    runProperties: CARunProperties = CARunProperties()
) {
    private val _figure: MutableStateFlow<CellularAutomatonFigure> = MutableStateFlow(figure)
    val figure: StateFlow<CellularAutomatonFigure> = _figure

    private val _rule: MutableStateFlow<CellularAutomatonRule> = MutableStateFlow(rule)
    val rule: StateFlow<CellularAutomatonRule> = _rule

    private val _cell: MutableStateFlow<CACell> = MutableStateFlow(cell)
    val cell: StateFlow<CACell> = _cell

    private val _field: MutableStateFlow<CAField> = MutableStateFlow(field)
    val field: StateFlow<CAField> = _field

    private val _runProperties: MutableStateFlow<CARunProperties> = MutableStateFlow(runProperties)
    val runProperties: StateFlow<CARunProperties> = _runProperties

    private val _transitionColors: MutableStateFlow<List<Color>> = MutableStateFlow(makeTransitionColors())
    val transitionColors: StateFlow<List<Color>> = _transitionColors

    private val _isRun: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRun: StateFlow<Boolean> = _isRun

    suspend fun setFigure(newFigure: CellularAutomatonFigure) {
        when (isRun.value) {
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
                height = height ?: figure.value.height
            )
        )
    }

    fun setRule(newRule: CellularAutomatonRule) {
        _rule.value = newRule
        _transitionColors.value = makeTransitionColors()
    }

    fun setCellParams(
        cellColor: Color? = null,
        agedCellColor: Color? = null,
        cellCornerRadius: Float? = null,
        marginsRatio: Float? = null,
    ) {
        val old = _cell.value
        _cell.value = CACell(
            cellColor = cellColor ?: old.cellColor,
            agedCellColor = agedCellColor ?: old.agedCellColor,
            cellCornerRadius = cellCornerRadius ?: old.cellCornerRadius,
            marginsRatio = marginsRatio ?: old.marginsRatio
        )
        if(cellColor != old.cellColor || agedCellColor != old.agedCellColor) {
            _transitionColors.value = makeTransitionColors()
        }
    }

    fun setFieldParams(
        isDrawGrid: Boolean? = null,
        isDrawable: Boolean? = null,
        isZoomable: Boolean? = null
    ) {
        val old = _field.value
        _field.value = CAField(
            isDrawGrid = isDrawGrid ?: old.isDrawGrid,
            isDrawable = isDrawable ?: old.isDrawable,
            isZoomable = isZoomable ?: old.isZoomable
        )
    }

    fun setRunProperties(
        delay: Long? = null,
        behaviour: CARunBehaviour? = null
    ) {
        val old = _runProperties.value
        _runProperties.value = CARunProperties(
            delay = delay ?: old.delay,
            behaviour = behaviour ?: old.behaviour
        )
    }

    private fun makeTransitionColors(): List<Color> {
        if (rule.value.aging == 0) {
            return listOf(_cell.value.cellColor)
        }
        val rTransitList = transitionListOfFloat(
            _cell.value.cellColor.red,
            _cell.value.agedCellColor.red,
            rule.value.aging
        )
        val gTransitList = transitionListOfFloat(
            _cell.value.cellColor.green,
            _cell.value.agedCellColor.green,
            rule.value.aging
        )
        val bTransitList = transitionListOfFloat(
            _cell.value.cellColor.blue,
            _cell.value.agedCellColor.blue,
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
        if (_isRun.value) {
            return
        }
        when(val behaviour = _runProperties.value.behaviour) {
            is CARunBehaviour.Custom -> customRun(behaviour)
            CARunBehaviour.Simple -> simpleRun()
        }
    }

    private suspend fun simpleRun() {
        _isRun.value = true
        while (_isRun.value) {
            _figure.value.nextStep(rule.value)
            delay(_runProperties.value.delay)
        }
    }

    private suspend fun customRun(behaviour: CARunBehaviour.Custom) {
        var remainingCycles = when(behaviour.cycles) {
            is CACycles.Finite -> (behaviour.cycles as CACycles.Finite).count
            CACycles.Infinite -> 0
        }
        _isRun.value = true
        while (_isRun.value) {
            val oldGeneration = figure.value.generation.cellsState.value
            _figure.value.nextStep(rule.value)
            if (oldGeneration == figure.value.generation.cellsState.value) {
                remainingCycles = (remainingCycles - 1).coerceAtLeast(-1)
                when (remainingCycles) {
                    0 -> {
                        stop()
                    }

                    else -> {
                        setFigure(behaviour.nextFigure())
                    }
                }
            }
            delay(_runProperties.value.delay)
        }
    }

    fun nextStep() {
        if (_isRun.value) {
            return
        }
        figure.value.nextStep(rule.value)
    }

    fun stop() {
        _isRun.value = false
    }
}


@Composable
fun CellularAutomato(
    state: CellularAutomatoState,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current
    var width by remember { mutableStateOf(1.dp) }
    var height by remember { mutableStateOf(1.dp) }
    var basedOnWidth by remember { mutableStateOf(true) }
    val cell = state.cell.collectAsState()
    val field = state.field.collectAsState()
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
    actualFigure: CellularAutomatonFigure,
    transitionColors: List<Color>,
    cells: State<CACell>,
    field: State<CAField>
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
        val cornerRadiusOnCanvas = cellSizeOnCanvas * cells.value.cellCornerRadius * .4f
        val margins = cellSizeOnCanvas - cellSizeOnCanvas * cells.value.marginsRatio
        val halfMargins = margins / 2
        generation.value.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                if (cell > 0) {
                    drawRoundRect(
                        color = transitionColors
                            .getOrElse(cell - 1) { cells.value.cellColor },
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
                    color = cells.value.cellColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height)
                )
            }
            repeat(actualFigure.height + 1) {
                val y = cellSizeOnCanvas * it
                drawLine(
                    color = cells.value.cellColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y)
                )
            }
        }
    }
}