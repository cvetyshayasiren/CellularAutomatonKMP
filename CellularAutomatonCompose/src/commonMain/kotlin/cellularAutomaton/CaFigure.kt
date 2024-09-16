package cellularAutomaton

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

@Stable
interface CaFigure {
    val generation: CAGeneration
    val height: Int
        get() = generation.cellsState.value.size
    val width: Int
        get() = generation.cellsState.value[0].size

    @Stable
    fun nextStep(rule: CaRule = CaRule()) {
        val cells = generation.cellsState.value
        val newGeneration = MutableList(cells.size) { MutableList(cells[0].size) { 0 } }
        cells.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                val neighbors = listOf<Int>(
                    cells.getMod(i - 1, j - 1),
                    cells.getMod(i - 1, j),
                    cells.getMod(i - 1, j + 1),
                    cells.getMod(i, j - 1),
                    cells.getMod(i, j + 1),
                    cells.getMod(i + 1, j - 1),
                    cells.getMod(i + 1, j),
                    cells.getMod(i + 1, j + 1),
                ).sumOf { if (it == 1) 1.toInt() else 0 }

                newGeneration[i][j] = when (cell) {
                    1 -> if (neighbors in rule.survive) 1 else if (rule.aging > 0) 2 else 0
                    0 -> if (neighbors in rule.born) 1 else 0
                    else -> if (cell < rule.aging) cell + 1 else 0
                }
            }
        }
        generation.setCellsState(newGeneration)
    }

    fun moreOrEqualThan(other: CaFigure) = width >= other.width && height >= other.height

    fun addFigure(x: Int = 0, y: Int = 0, figure: CaFigure) {
        require(this.moreOrEqualThan(figure)) { "the figure should be smaller" }
        require(x in 0 until width && y in 0 until height) { "x or y out of range" }

        val newGeneration = generation.cellsState.value.toMutableList().map { it.toMutableList() }
        figure.generation.cellsState.value.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                newGeneration[(i + y) % height][(j + x) % width] = cell
            }
        }
        generation.setCellsState(newGeneration)
    }

    fun addFigureInCenter(figure: CaFigure) {
        val x = width / 2 - figure.width / 2
        val y = height / 2 - figure.height / 2
        addFigure(x, y, figure)
    }

    fun switchCell(x: Int, y: Int) {
        require(x in 0 until width && y in 0 until height) { "x or y out of range" }
        when (generation.cellsState.value[y][x]) {
            0 -> addFigure(x, y, One())
            else -> addFigure(x, y, Zero())
        }
    }

    fun getRandomCellsState(fillingRatio: Float? = .5f): List<List<Int>> {
        val unitsCount = (width * height * (fillingRatio ?: Random.nextFloat())).toInt()
            .coerceIn(0, width*height)
        val nullsCount = width * height - unitsCount
        val cells = (List(unitsCount) { 1 } + List(nullsCount) { 0 }).shuffled().iterator()
        return List(height) { List(width) { cells.next() } }
    }

    fun randomiseState(fillingRatio: Float? = .5f) {
        generation.setCellsState(getRandomCellsState(fillingRatio))
    }

    fun clearState() {
        generation.setCellsState(List(height) { List(width) { 0 } })
    }


    //IMPLEMENTATIONS
    class Zero: CaFigure {
        override val generation: CAGeneration = CAGeneration(listOf(listOf(0)))
    }

    class One: CaFigure {
        override val generation: CAGeneration = CAGeneration(listOf(listOf(1)))
    }

    class FromGeneration(override val generation: CAGeneration): CaFigure

    class Rectangle(
        override val width: Int = 2,
        override val height: Int = 2,
    ): CaFigure {
        override val generation: CAGeneration = CAGeneration(List(height) { List(width) { 1 } })
    }

    class FromRandom(
        override val width: Int = 20,
        override val height: Int = 20,
        private val fillingRatio: Float? = .5f,
    ): CaFigure {
        override val generation: CAGeneration = CAGeneration(getRandomCellsState(fillingRatio))
    }

    class FillRandomise(
        private val size: StateFlow<IntSize>,
        private val fillingRatio: Float? = .5f,
        private val cellSize: Int = 100,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): CaFigure {
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

    class FromImageBitmap(
        private val imageBitmap: ImageBitmap,
        private val aging: Int = 0,
        private val scale: Float = 1f
    ): CaFigure {
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
}