package eu.barcode_1d_improve.ui.restoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import eu.barcode_1d_improve.data.DetectedBarcode

@Composable
fun BarcodeOverlay(
	barcodes: List<DetectedBarcode>,
	imageWidth: Int,
	modifier: Modifier = Modifier,
                  ) {
	Canvas(modifier) {
		val scale = size.width / imageWidth
		barcodes.forEach { barcode ->
			if (barcode.corners.size < 3) return@forEach
			val path = Path().apply {
				barcode.corners.forEachIndexed { i, p ->
					val x = p.x * scale
					val y = p.y * scale
					if (i == 0) moveTo(x, y) else lineTo(x, y)
				}
				close()
			}
			drawPath(path, Color.Green, style = Stroke(width = 3.dp.toPx()))
		}
	}
}