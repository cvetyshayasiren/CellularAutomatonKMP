package cellularAutomaton

sealed class CACycles {
    data object Infinite: CACycles()
    data class Finite(val count: Int = 1): CACycles() {
        init {
            require(count > 0) { "The number of cycles must be a positive integer" }
        }
    }
}

sealed class CARunBehaviour {
    data object Simple: CARunBehaviour()
    open class Custom(
        open val cycles: CACycles = CACycles.Infinite,
        val nextCycleFigure: () -> CellularAutomatonFigure = { CAZeroFigure() },
        val onCycleStart: (() -> Unit)? = null

    ): CARunBehaviour() {
        open fun nextFigure(): CellularAutomatonFigure {
            onCycleStart?.let { it() }
            return nextCycleFigure()
        }
    }

    class RandomFigure(
        override val cycles: CACycles = CACycles.Infinite,
        private val width: Int = 24,
        private val height: Int = 24,
        private val fillingRatio: Float? = null,
        onCycleStart: (() -> Unit)? = null
    ): Custom(cycles = cycles, onCycleStart = onCycleStart) {
        override fun nextFigure(): CellularAutomatonFigure {
            return CARandomFigure(
                width = width,
                height = height,
                fillingRatio = fillingRatio
            )
        }
    }

    class SameFigure(
        override val cycles: CACycles,
        private val figure: CellularAutomatonFigure,
        onCycleStart: (() -> Unit)? = null
    ): Custom(
        cycles = cycles,
        nextCycleFigure = { figure },
        onCycleStart = onCycleStart
    ) {
        override fun nextFigure(): CellularAutomatonFigure {
            return figure
        }
    }
}