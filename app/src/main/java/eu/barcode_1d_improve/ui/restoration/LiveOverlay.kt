package eu.barcode_1d_improve.ui.restoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LiveOverlay(frame: FrameResult?,  verified: Boolean, modifier: Modifier = Modifier) {
	Canvas(modifier) {
		val f = frame ?: return@Canvas
		val scale = maxOf(size.width / f.width, size.height / f.height)
		val dx = (size.width - f.width * scale) / 2f
		val dy = (size.height - f.height * scale) / 2f
		f.barcodes.forEach { b ->
			if (b.corners.size < 3) return@forEach
			val path = Path().apply {
				b.corners.forEachIndexed { i, p ->
					val x = p.x * scale + dx
					val y = p.y * scale + dy
					if (i == 0) moveTo(x, y) else lineTo(x, y)
				}
				close()
			}
/* 			drawPath(
				path = path,
				color = Color.Green,
				style = Stroke(width = 3.dp.toPx()),
			        ) */

			drawPath(path, if (verified) Color.Green else Color.Yellow, style = Stroke(width = 3.dp.toPx()))
		}
	}
}