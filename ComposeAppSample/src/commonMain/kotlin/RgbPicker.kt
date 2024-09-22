import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import theme.defaultPadding
import theme.defaultShadow
import theme.defaultShape
import theme.defaultShapeCorner

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RgbPicker(
    label: String,
    tint: Color,
    onClick: (color: Color) -> Unit
) {
    var alert by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(defaultPadding)
            .shadow(elevation = defaultShadow, shape = defaultShape)
            .clip(defaultShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 2.dp, color = tint, shape = defaultShape)
            .size(120.dp, 48.dp)
            .clickable { alert = true }
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(start = defaultPadding * 2)
                .basicMarquee(),
            text = label,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = defaultPadding * 4)
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = defaultShapeCorner,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = defaultShapeCorner
                    )
                )
                .background(tint)
        )
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