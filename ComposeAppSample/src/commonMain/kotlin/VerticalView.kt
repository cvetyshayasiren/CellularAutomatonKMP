import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cellularAutomaton.CellularAutomaton
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.launch
import theme.defaultShapeCorner
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalView(caState: CellularAutomatonState, size: IntSize) {
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val buttonHeight = with(LocalDensity.current) { (size.width * .2f).toDp() }
    val drawHeight = .8f

    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topStart = defaultShapeCorner, topEnd = defaultShapeCorner),
        scaffoldState = scaffoldState,
        sheetDragHandle = {
            StartButtonView(caState, buttonHeight)
        },
        sheetPeekHeight = buttonHeight,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxHeight(.7f)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(scrollState)
            ) {
                ControlView(caState)
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
        CellularAutomaton(
            state = caState,
            modifier = Modifier
                .fillMaxHeight(drawHeight)
                .fillMaxWidth()
                .then(caModifier())
        )
    }
}