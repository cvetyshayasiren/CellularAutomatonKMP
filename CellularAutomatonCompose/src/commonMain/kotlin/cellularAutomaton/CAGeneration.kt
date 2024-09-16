package cellularAutomaton

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class CAGeneration(private val firstGeneration: List<List<Int>>) {
    init {
        require(firstGeneration.isNotEmpty() &&
                firstGeneration[0].isNotEmpty() &&
                firstGeneration.all { firstGeneration[0].size == it.size }
        ) {
            "It should be a list of non-empty lists of the same length"
        }
    }
    private val _cellsState: MutableStateFlow<List<List<Int>>> = MutableStateFlow(firstGeneration)
    val cellsState: StateFlow<List<List<Int>>> = _cellsState

    fun setCellsState(newGeneration: List<List<Int>>) {
        _cellsState.value = newGeneration
    }
}

@Stable
fun List<List<Int>>.getMod(i: Int, j: Int): Int {
    val ii =
        if (i >= 0) i % this.size else (this.size - (kotlin.math.abs(i) % this.size)) % this.size
    val jj =
        if (j >= 0) j % this[ii].size else (this[ii].size - (kotlin.math.abs(j) % this[ii].size)) % this[ii].size
    return this[ii][jj]
}
