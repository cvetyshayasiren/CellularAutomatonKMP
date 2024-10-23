package cellularAutomaton

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CaCycles {
    data object Infinite: CaCycles()
    data class Finite(val count: Int = 1): CaCycles() {
        init {
            require(count > 0) { "The number of cycles must be a positive integer" }
        }

        private val _currentStep: MutableStateFlow<Int> = MutableStateFlow(0)
        val currentStep: StateFlow<Int> = _currentStep

        private val _currentCycle: MutableStateFlow<Int> = MutableStateFlow(0)
        val currentCycle: StateFlow<Int> = _currentCycle

        private val _cycleLength: MutableStateFlow<Int?> = MutableStateFlow(null)
        val cycleLength: StateFlow<Int?> = _cycleLength

        fun increase(isNextCycle: Boolean) {
            when(isNextCycle) {
                true -> {
                    _cycleLength.value = maxOf(_cycleLength.value ?: 0, _currentStep.value)
                    _currentStep.value = 0
                    _currentCycle.value += 1
                }
                false -> { _currentStep.value += 1 }
            }
        }
    }
}

sealed class CaRunBehaviour {
    data object Simple: CaRunBehaviour()

    interface Cycled { val cycles: CaCycles }

    class CycledRandom(override val cycles: CaCycles = CaCycles.Infinite):
        CaRunBehaviour(), Cycled
    class CycledSameFigure(override val cycles: CaCycles = CaCycles.Infinite):
        CaRunBehaviour(), Cycled

    open class Custom(
        override val cycles: CaCycles = CaCycles.Infinite,
        val nextCycleFigure: () -> CaFigure = { CaFigure.Zero },
        val onCycleStart: (() -> Unit)? = null

    ): CaRunBehaviour(), Cycled {
        open fun nextFigure(): CaFigure {
            onCycleStart?.let { it() }
            return nextCycleFigure()
        }
    }
}