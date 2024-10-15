import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.CellularAutomatonTheme
import view.CaView

@Composable
@Preview
fun App() {
    CellularAutomatonTheme {
        Surface {
            CaView()
        }
    }
}