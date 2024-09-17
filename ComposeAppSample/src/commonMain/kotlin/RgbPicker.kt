import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import theme.defaultPadding
import theme.defaultShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RgbPicker(
    label: String,
    tint: Color,
    onClick: (color: Color) -> Unit
) {
    var alert by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = label, fontSize = MaterialTheme.typography.labelMedium.fontSize)
        IconButton(
            onClick = { alert = true }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "",
                tint = tint
            )
        }
    }

    if(alert) {
        BasicAlertDialog(
            onDismissRequest = { alert = false },
            content = {
                Column(
                    modifier = Modifier
                        .clip(defaultShape)
                        .background(tint.copy(alpha = .9f))
                        .padding(defaultPadding * 2),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = tint.red,
                        valueRange = (0f..1f),
                        onValueChange = {
                            onClick(tint.copy(red = it))
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Red,
                            inactiveTrackColor = Color.Red.copy(alpha = .1f)
                        ),
                        thumb = { MyThumb((tint.red * 255).toInt(), Color.Red) }
                    )
                    Slider(
                        value = tint.green,
                        valueRange = (0f..1f),
                        onValueChange = {
                            onClick(tint.copy(green = it))
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Green,
                            inactiveTrackColor = Color.Green.copy(alpha = .1f)
                        ),
                        thumb = { MyThumb((tint.green * 255).toInt(), Color.Green) }
                    )
                    Slider(
                        value = tint.blue,
                        valueRange = (0f..1f),
                        onValueChange = {
                            onClick(tint.copy(blue = it))
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Blue,
                            inactiveTrackColor = Color.Blue.copy(alpha = .1f)
                        ),
                        thumb = { MyThumb((tint.blue * 255).toInt(), Color.Blue) }
                    )
                    Row(
                        Modifier.fillMaxWidth(.9f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                onClick(randomColor())
                            }
                        ) {
                            Text("\uD83C\uDFB2")
                        }
                        TextButton(
                            onClick = {
                                alert = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = tint
                            )
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        )
    }
}