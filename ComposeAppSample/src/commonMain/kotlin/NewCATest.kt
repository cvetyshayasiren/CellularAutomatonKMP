import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cellularAutomaton.CellularAutomaton
import cellularAutomaton.CellularAutomatonState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NewCaTest() {
    val caState = remember { CellularAutomatonState() }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CellularAutomaton(caState, Modifier.size(400.dp))
        TextButton(
            onClick = {
                when(caState.isRun.value) {
                    false -> CoroutineScope(Dispatchers.Default).launch { caState.run() }
                    true -> caState.stop()
                }
            }
        ) {
            Text("run/stop")
        }
    }
}