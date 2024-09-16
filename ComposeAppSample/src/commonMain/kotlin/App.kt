import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.CellularAutomatonTheme

@Composable
@Preview
fun App() {
    CellularAutomatonTheme {
        Surface {
            ModifierProperties
                .setBackground(MaterialTheme.colorScheme.surface)
            CaView()
        }
    }
}