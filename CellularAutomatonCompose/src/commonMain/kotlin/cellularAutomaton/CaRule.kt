package cellularAutomaton

import androidx.compose.runtime.Stable

@Stable
data class CaRule(
    val born: Set<Int> = setOf(3),
    val survive: Set<Int> = setOf(2, 3),
    val aging: Int = 0
) {
    init {
        require(born.all { it in 0..8 } &&
                survive.all { it in 0..8 } &&
                aging >= 0) {
            "The values of the lists must be in the ranges 1..8, aging must be >= 0"
        }
    }

    override fun toString(): String {
        return "B${born.joinToString("")}" +
                "S${survive.joinToString("")}/${aging}"
    }

    companion object {
        fun fromString(string: String): CaRule {
            val born = "B(\\d*)S".toRegex().find(string)?.groupValues?.get(1)
            val survive = "S(\\d*)/".toRegex().find(string)?.groupValues?.get(1)
            val aging = "/(\\d*)$".toRegex().find(string)?.groupValues?.get(1)
            require(born != null && survive != null && aging != null) { "wrong string" }
            val b = born.split("").filterNot { it.isBlank() }.map { it.toInt() }.toSet()
            val s = survive.split("").filterNot { it.isBlank() }.map { it.toInt() }.toSet()
            return CaRule(b, s, aging.toInt())
        }
    }
}