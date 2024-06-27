package cellularAutomaton

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

actual fun ImageBitmap.scale(newWidth: Int, newHeight: Int): ImageBitmap {
    val inputImage = this.toAwtImage()
    val scaledImage = BufferedImage(newWidth, newHeight, inputImage.type)
    val graphics2D: Graphics2D = scaledImage.createGraphics()
    graphics2D.drawImage(
        inputImage.getScaledInstance(
            newWidth,
            newHeight,
            Image.SCALE_SMOOTH
        ), 0, 0, newWidth, newHeight, null
    )
    graphics2D.dispose()
    return scaledImage.toComposeImageBitmap()
}