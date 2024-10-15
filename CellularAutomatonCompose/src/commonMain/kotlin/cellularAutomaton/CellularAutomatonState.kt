package cellularAutomaton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberCellularAutomatonState(
    figure: CaFigure = CaFigure(),
    rule: CaRule = CaRule(),
    cellState: CaCellState = CaCellState(),
    fieldState: CaFieldState = CaFieldState(),
    runProperties: CaRunProperties = CaRunProperties()

): CellularAutomatonState {
    return remember {
        CellularAutomatonState(
            figure = figure,
            rule = rule,
            cellState = cellState,
            fieldState = fieldState,
            runProperties = runProperties
        )
    }
}
@Stable
data class CaCellState(
    val color: Color = Color(96, 108, 56),
    val agedColor: Color = Color(221, 161, 94),
    val cornerRadius: Float = .5f,
    val marginsRatio: Float = .9f,
)

@Stable
data class CaFieldState(
    val isDrawGrid: Boolean = false,
    val isDrawable: Boolean = false,
    val isZoomable: Boolean = false
)

@Stable
data class CaRunProperties(
    val delay: Long = 100,
    val behaviour: CaRunBehaviour = CaRunBehaviour.Simple
)

@Stable
class CellularAutomatonState(
    val figure: CaFigure = CaFigure(),
    rule: CaRule = CaRule(),
    cellState: CaCellState = CaCellState(),
    fieldState: CaFieldState = CaFieldState(),
    runProperties: CaRunProperties = CaRunProperties()
) {
    private val _rule: MutableStateFlow<CaRule> = MutableStateFlow(rule)
    val rule: StateFlow<CaRule> = _rule

    private val _cellState: MutableStateFlow<CaCellState> = MutableStateFlow(cellState)
    val cellState: StateFlow<CaCellState> = _cellState

    private val _fieldState: MutableStateFlow<CaFieldState> = MutableStateFlow(fieldState)
    val fieldState: StateFlow<CaFieldState> = _fieldState

    private val _runProperties: MutableStateFlow<CaRunProperties> = MutableStateFlow(runProperties)
    val runProperties: StateFlow<CaRunProperties> = _runProperties

    private val _transitionColors: MutableStateFlow<List<Color>> = MutableStateFlow(makeTransitionColors())
    val transitionColors: StateFlow<List<Color>> = _transitionColors

    private val _isRun: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRun: StateFlow<Boolean> = _isRun

    suspend fun setFigure(newFigure: CaFigure) {
        when (isRun.value) {
            true -> {
                stop()
                figure.replaceFigure(newFigure)
                _isRun.value = true
            }

            false -> {
                figure.replaceFigure(newFigure)
            }
        }
    }

    suspend fun setSize(width: Int? = null, height: Int? = null) {
        stop()
        setFigure(
            CaFigure.FromRandom(
                width = width ?: figure.size.value.width,
                height = height ?: figure.size.value.height
            )
        )
    }

    fun setRule(newRule: CaRule) {
        _rule.value = newRule
        _transitionColors.value = makeTransitionColors()
    }

    fun setCellParams(
        color: Color? = null,
        agedColor: Color? = null,
        cornerRadius: Float? = null,
        marginsRatio: Float? = null,
    ) {
        val old = _cellState.value
        _cellState.value = CaCellState(
            color = color ?: old.color,
            agedColor = agedColor ?: old.agedColor,
            cornerRadius = cornerRadius ?: old.cornerRadius,
            marginsRatio = marginsRatio ?: old.marginsRatio
        )
        if(color != old.color || agedColor != old.agedColor) {
            _transitionColors.value = makeTransitionColors()
        }
    }

    fun setFieldParams(
        isDrawGrid: Boolean? = null,
        isDrawable: Boolean? = null,
        isZoomable: Boolean? = null
    ) {
        val old = _fieldState.value
        _fieldState.value = CaFieldState(
            isDrawGrid = isDrawGrid ?: old.isDrawGrid,
            isDrawable = isDrawable ?: old.isDrawable,
            isZoomable = isZoomable ?: old.isZoomable
        )
    }

    fun setRunProperties(
        delay: Long? = null,
        behaviour: CaRunBehaviour? = null
    ) {
        val old = _runProperties.value
        _runProperties.value = CaRunProperties(
            delay = delay ?: old.delay,
            behaviour = behaviour ?: old.behaviour
        )
    }

    private fun makeTransitionColors(): List<Color> {
        if (rule.value.aging == 0) {
            return listOf(_cellState.value.color)
        }
        val rTransitList = transitionListOfFloat(
            _cellState.value.color.red,
            _cellState.value.agedColor.red,
            rule.value.aging
        )
        val gTransitList = transitionListOfFloat(
            _cellState.value.color.green,
            _cellState.value.agedColor.green,
            rule.value.aging
        )
        val bTransitList = transitionListOfFloat(
            _cellState.value.color.blue,
            _cellState.value.agedColor.blue,
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
            is CaRunBehaviour.Custom -> customRun(behaviour)
            CaRunBehaviour.Simple -> simpleRun()
        }
    }

    private suspend fun simpleRun() {
        _isRun.value = true
        while (_isRun.value) {
            figure.nextGeneration(rule.value)
            delay(_runProperties.value.delay)
        }
    }

    private suspend fun customRun(behaviour: CaRunBehaviour.Custom) {
        var remainingCycles = when(behaviour.cycles) {
            is CaCycles.Finite -> (behaviour.cycles as CaCycles.Finite).count
            CaCycles.Infinite -> 0
        }
        _isRun.value = true
        while (_isRun.value) {
            val oldGeneration = figure.generation.value
            figure.nextGeneration(rule.value)
            if (oldGeneration == figure.generation.value) {
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

    fun nextGeneration() {
        if (_isRun.value) {
            return
        }
        figure.nextGeneration(rule.value)
    }

    fun stop() {
        _isRun.value = false
    }
}