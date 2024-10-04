package cellularAutomaton

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

@Stable
class CaFigure(
    generation: List<List<Int>>
) {
    init {
        require(generation.isNotEmpty() &&
                generation[0].isNotEmpty() &&
                generation.all { generation[0].size == it.size }
        ) {
            "It should be a list of non-empty lists of the same length"
        }
    }

    private val _generation: MutableStateFlow<List<List<Int>>> = MutableStateFlow(generation)
    val generation: StateFlow<List<List<Int>>> = _generation

    private val _size: MutableStateFlow<IntSize> = MutableStateFlow(getSize())
    val size: StateFlow<IntSize> = _size

    fun replaceFigure(newGeneration: List<List<Int>>) {
        _generation.value = newGeneration
        _size.value = getSize()
    }

    fun replaceFigure(figure: CaFigure) {
        replaceFigure(newGeneration = figure.generation.value)
    }

    private fun getSize() = IntSize(
        width = generation.value[0].size,
        height = generation.value.size
    )
    @Stable
    fun nextGeneration(rule: CaRule = CaRule()) {
        val cells = _generation.value
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
        replaceFigure(newGeneration)
    }

    fun moreOrEqualThan(other: CaFigure) = _size.value.width >= other._size.value.width &&
            _size.value.height >= other._size.value.height

    fun addFigure(x: Int = 0, y: Int = 0, figure: CaFigure) {
        require(this.moreOrEqualThan(figure)) { "the figure should be smaller" }
        require(
            x in 0 until _size.value.width && y in 0 until _size.value.height
        ) { "x or y out of range" }

        val newGeneration = generation.value.toMutableList().map { it.toMutableList() }
        figure.generation.value.forEachIndexed { i, line ->
            line.forEachIndexed { j, cell ->
                newGeneration[(i + y) % _size.value.height][(j + x) % _size.value.width] = cell
            }
        }
        replaceFigure(newGeneration)
    }

    fun addFigureInCenter(figure: CaFigure) {
        val x = _size.value.width / 2 - figure._size.value.width / 2
        val y = _size.value.height / 2 - figure._size.value.height / 2
        addFigure(x, y, figure)
    }

    fun switchCell(x: Int, y: Int) {
        require(
            x in 0 until _size.value.width && y in 0 until _size.value.height
        ) { "x or y out of range" }
        when (generation.value[y][x]) {
            0 -> addFigure(x, y, One)
            else -> addFigure(x, y, Zero)
        }
    }

    fun getRandomCellsState(fillingRatio: Float? = .5f): List<List<Int>> {
        return getRandomCellsState(fillingRatio, _size.value)
    }

    fun randomiseState(fillingRatio: Float? = null) {
        replaceFigure(getRandomCellsState(fillingRatio))
    }

    fun clearState() {
        replaceFigure(List(_size.value.height) { List(_size.value.width) { 0 } })
    }


    companion object {
        val Zero = Rectangle(1, 1, 0)
        val One = Rectangle(1, 1, 1)

        fun getRandomCellsState(
            fillingRatio: Float? = .5f,
            width: Int = 20,
            height: Int = 20
        ): List<List<Int>> {
            val unitsCount = (width * height *
                    (fillingRatio ?: Random.nextFloat())).toInt()
                .coerceIn(0, width * height)
            val nullsCount = width * height - unitsCount
            val cells = (List(unitsCount) { 1 } + List(nullsCount) { 0 }).shuffled().iterator()
            return List(height) { List(width) { cells.next() } }
        }

        fun getRandomCellsState(
            fillingRatio: Float? = .5f,
            size: IntSize = IntSize(20, 20)
        ): List<List<Int>> {
            return getRandomCellsState(fillingRatio, size.width, size.height)
        }

        fun Rectangle(
            width: Int = 2,
            height: Int = 2,
            value: Int = 1
        ): CaFigure {
            return CaFigure(List(height) { List(width) { value } })
        }

        fun FromRandom(
            width: Int = 20,
            height: Int = 20,
            fillingRatio: Float? = .5f,
        ): CaFigure {
            return CaFigure(getRandomCellsState(fillingRatio, width, height))
        }

        fun FillRandomise(
            canvasSize: StateFlow<IntSize>,
            cellSize: Int = 100,
            fillingRatio: Float? = .5f,
        ): CaFigure {
            val width = (canvasSize.value.width/cellSize).coerceAtLeast(1)
            val height = (canvasSize.value.height/cellSize).coerceAtLeast(1)
            return CaFigure(getRandomCellsState(fillingRatio, width, height))
        }

        fun FromImageBitmap(
            imageBitmap: ImageBitmap,
            aging: Int = 0,
            scale: Float = 1f
        ): CaFigure {
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
            return CaFigure(figureList)
        }
    }
}

@Stable
fun List<List<Int>>.getMod(i: Int, j: Int): Int {
    val ii =
        if (i >= 0) i % this.size else (this.size - (abs(i) % this.size)) % this.size
    val jj =
        if (j >= 0) j % this[ii].size else (this[ii].size - (abs(j) % this[ii].size)) % this[ii].size
    return this[ii][jj]
}