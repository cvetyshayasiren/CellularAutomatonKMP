package cellularAutomaton

import kotlin.random.Random

interface CellularAutomatonFigure {
    val generation: CAGeneration
    val height: Int
        get() = generation.cellsState.value.size
    val width: Int
        get() = generation.cellsState.value[0].size

    fun nextStep(rule: CellularAutomatonRule = CellularAutomatonRule()) {
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

    fun moreOrEqualThan(other: CellularAutomatonFigure) = width >= other.width && height >= other.height

    fun addFigure(x: Int = 0, y: Int = 0, figure: CellularAutomatonFigure) {
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

    fun addFigureInCenter(figure: CellularAutomatonFigure) {
        val x = width / 2 - figure.width / 2
        val y = height / 2 - figure.height / 2
        addFigure(x, y, figure)
    }

    fun switchCell(x: Int, y: Int) {
        require(x in 0 until width && y in 0 until height) { "x or y out of range" }
        when (generation.cellsState.value[y][x]) {
            0 -> addFigure(x, y, CAOneFigure())
            else -> addFigure(x, y, CAZeroFigure())
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
}