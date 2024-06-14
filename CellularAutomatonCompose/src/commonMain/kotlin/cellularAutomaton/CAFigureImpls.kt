package cellularAutomaton

import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CABasicFigure(override val generation: CAGeneration): CellularAutomatonFigure

class CARandomFigure(
    override val width: Int = 20,
    override val height: Int = 20,
    private val fillingRatio: Float? = .5f,
): CellularAutomatonFigure {
    override val generation: CAGeneration = CAGeneration(getRandomCellsState(fillingRatio))
}

class CAZeroFigure(
    override val generation: CAGeneration = CAGeneration(listOf(listOf(0)))
): CellularAutomatonFigure

class CAOneFigure(
    override val generation: CAGeneration = CAGeneration(listOf(listOf(1)))
): CellularAutomatonFigure

class CAFillRandomFigure(
    private val size: StateFlow<IntSize>,
    private val fillingRatio: Float? = .5f,
    private val cellSize: Int = 100,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): CellularAutomatonFigure {
    override var width: Int = (size.value.width/cellSize).coerceAtLeast(1)
    override var height: Int = (size.value.height/cellSize).coerceAtLeast(1)
    init {
        scope.launch {
            size.collect {
                width = it.width/cellSize
                height = it.height/cellSize
                generation.setCellsState(getRandomCellsState(fillingRatio))
            }
        }
    }

    override val generation: CAGeneration = CAGeneration(getRandomCellsState(fillingRatio))
}
