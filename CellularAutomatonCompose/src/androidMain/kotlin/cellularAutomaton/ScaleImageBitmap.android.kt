package cellularAutomaton

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ImageBitmap.scale(newWidth: Int, newHeight: Int): ImageBitmap {
    return Bitmap
        .createScaledBitmap(this.asAndroidBitmap(), newWidth, newHeight, false)
        .asImageBitmap()
}