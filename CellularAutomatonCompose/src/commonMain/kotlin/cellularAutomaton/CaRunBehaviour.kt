package cellularAutomaton

sealed class CaCycles {
    data object Infinite: CaCycles()
    data class Finite(val count: Int = 1): CaCycles() {
        init {
            require(count > 0) { "The number of cycles must be a positive integer" }
        }
    }
}

sealed class CaRunBehaviour {
    data object Simple: CaRunBehaviour()
    open class Custom(
        open val cycles: CaCycles = CaCycles.Infinite,
        val nextCycleFigure: () -> CaFigure = { CaFigure.Zero },
        val onCycleStart: (() -> Unit)? = null

    ): CaRunBehaviour() {
        open fun nextFigure(): CaFigure {
            onCycleStart?.let { it() }
            return nextCycleFigure()
        }
    }

    class RandomFigure(
        override val cycles: CaCycles = CaCycles.Infinite,
        private val width: Int = 24,
        private val height: Int = 24,
        private val fillingRatio: Float? = null,
        onCycleStart: (() -> Unit)? = null
    ): Custom(cycles = cycles, onCycleStart = onCycleStart) {
        override fun nextFigure(): CaFigure {
            return CaFigure.FromRandom(
                width = width,
                height = height,
                fillingRatio = fillingRatio
            )
        }
    }

    class SameFigure(
        override val cycles: CaCycles,
        private val figure: CaFigure,
        onCycleStart: (() -> Unit)? = null
    ): Custom(
        cycles = cycles,
        nextCycleFigure = { figure },
        onCycleStart = onCycleStart
    ) {
        override fun nextFigure(): CaFigure {
            return figure
        }
    }
}