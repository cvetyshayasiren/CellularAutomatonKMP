package cellularAutomaton

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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

class CAFigureFromImageBitmap(
    private val imageBitmap: ImageBitmap,
    private val aging: Int = 0,
    private val scale: Float = 1f
    ): CellularAutomatonFigure {
    override val generation: CAGeneration = bitmapToGeneration()
    private fun bitmapToGeneration(): CAGeneration {

        val newWidth = (imageBitmap.width * scale).toInt()
        val newHeight = (imageBitmap.height * scale).toInt()
        val scaledImage = imageBitmap.scale(newWidth, newHeight)

        val pixelsArray = IntArray(newWidth * newHeight)
        scaledImage.readPixels(pixelsArray)
        val figureList = MutableList(newHeight) { MutableList(newWidth) { 0 } }
        pixelsArray.forEachIndexed { index, pixel ->
            Color(pixel).let { color ->
                val average = (color.red + color.blue + color.green) / 3
                val cell = when(aging) {
                    0 -> if (average < .5) 0 else 1
                    else -> (aging * average).toInt()
                }
                figureList[index / newWidth][index % newWidth] = cell
            }
        }
        return CAGeneration(figureList)
    }
}