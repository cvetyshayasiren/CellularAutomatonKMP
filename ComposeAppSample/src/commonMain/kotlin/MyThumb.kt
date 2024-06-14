import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MyThumb(value: Int? = null, color: Color = MaterialTheme.colorScheme.primary) {
    Box(
        Modifier
            .size(60.dp, 30.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if(value != null) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = value.toString(),
                color = MaterialTheme.colorScheme.surface,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}