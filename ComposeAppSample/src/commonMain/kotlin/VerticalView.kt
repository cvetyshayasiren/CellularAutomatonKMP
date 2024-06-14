import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cellularAutomaton.CellularAutomaton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalView(ca: CellularAutomaton, size: IntSize) {
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val buttonHeight = with(LocalDensity.current) { (size.width * .2f).toDp() }
    val drawHeight = animateFloatAsState(
        if(scaffoldState.bottomSheetState.hasExpandedState) .9f else .5f
    )
    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        scaffoldState = scaffoldState,
        sheetDragHandle = {
            StartButtonView(ca, buttonHeight)
        },
        sheetPeekHeight = buttonHeight,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxHeight(.7f)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(scrollState)
            ) {
                ControlView(ca)
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                onClick = {
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "")
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxHeight(drawHeight.value)
                .padding(20.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) { ca.Draw() }
    }
}