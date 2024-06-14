package cellularAutomaton

interface CircularRunBehaviour {
    val cycles: Int?
    fun nextFigure(): CellularAutomatonFigure
}

class CircularRunRandomBehaviour(
    override val cycles: Int? = null,
    val width: Int,
    val height: Int,
    private val fillingRatio: Float? = null
): CircularRunBehaviour {
    override fun nextFigure(): CellularAutomatonFigure {
        return CARandomFigure(
            width = width,
            height = height,
            fillingRatio = fillingRatio
        )
    }
}

class CircularRunSameFigureBehaviour(
    override val cycles: Int? = null,
    private val figure: CellularAutomatonFigure
): CircularRunBehaviour {
    override fun nextFigure(): CellularAutomatonFigure {
        return figure
    }
}
