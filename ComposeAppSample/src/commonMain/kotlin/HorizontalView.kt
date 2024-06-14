import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cellularAutomaton.CellularAutomaton

@Composable
fun HorizontalView(ca: CellularAutomaton, size: IntSize) {
    val scrollState = rememberScrollState()
    val height = animateDpAsState(
        when(scrollState.value) {
            in(0..100) -> with(LocalDensity.current) {
                (size.height/3).toDp() - scrollState.value.toDp() }
            else -> with(LocalDensity.current) { (size.height/8).toDp() }
        }
    )
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(with(LocalDensity.current) { (size.width/2).toDp() })
                .padding(20.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) { ca.Draw() }
        Column(
            Modifier
                .padding(20.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            StartButtonView(ca, height.value)
            Box(Modifier.verticalScroll(scrollState)) { ControlView(ca) }
        }
    }
}