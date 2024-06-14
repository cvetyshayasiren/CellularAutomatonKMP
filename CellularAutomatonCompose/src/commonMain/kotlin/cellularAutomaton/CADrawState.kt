package cellularAutomaton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CADrawState(
    val backgroundColor: Color = Color(254, 250, 224),
    val primaryCellColor: Color = Color(96, 108, 56),
    val secondaryCellColor: Color = Color(221, 161, 94),
    val delay: Long = 100,
    val cornerRadius: Float = .5f,
    val drawGrid: Boolean = false,
    val marginsRatio: Float = .9f,
    val isDrawable: Boolean = false,
    val isZoomable: Boolean = false,
    val padding: Dp = 0.dp,
    val shape: Shape = RoundedCornerShape(0.dp)
)
