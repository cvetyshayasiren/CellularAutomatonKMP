import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cellularAutomaton.CAFigureFromImageBitmap
import cellularAutomaton.CellularAutomatonFigure
import cellularAutomaton.imageBitmapFromBytes
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FilePickerButton(
    aging: Int = 0,
    onPick: suspend(figure: CellularAutomatonFigure)-> Unit
) {
    var imageText by remember { mutableStateOf("load image") }

    val fileType = listOf("jpg", "png")

    val launcher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        CoroutineScope(Dispatchers.IO).launch {
            file?.let { platformFile ->
                onPick(
                    CAFigureFromImageBitmap(
                        imageBitmap = imageBitmapFromBytes(platformFile.readBytes()),
                        aging = aging,
                        scale = .2f
                    )
                )
                imageText = platformFile.name
            }
        }
    }

    Button(
        modifier = Modifier.fillMaxWidth(.8f),
        shape = RoundedCornerShape(8.dp),
        onClick = { launcher.launch() }
    ) {
        Text(imageText)
    }
}