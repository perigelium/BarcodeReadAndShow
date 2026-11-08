package eu.barcode_1d_improve.ui.restoration

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import eu.barcode_1d_improve.data.GeneratedBarcode

@Composable
fun FullScreenBarcode(barcode: GeneratedBarcode, onClose: () -> Unit) {
	BackHandler(onBack = onClose)

	val context = LocalContext.current
	DisposableEffect(Unit) {
		val window = context.findActivity()?.window
		window?.attributes = window.attributes?.apply { screenBrightness = 1f }
		window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		onDispose {
			window?.attributes = window.attributes?.apply {
				screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
			}
			window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		}
	}

	Box(Modifier.fillMaxSize().background(Color.White).systemBarsPadding()) {
		TextButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd)) {
			Text("Close", color = Color.Black)
		}
		Column(
			Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
		      ) {
			Image(
				bitmap = barcode.bitmap.asImageBitmap(),
				contentDescription = "Barcode ${barcode.value}",
				contentScale = ContentScale.FillWidth,
				filterQuality = FilterQuality.None,
				modifier = Modifier.fillMaxWidth(),
			     )
			Spacer(Modifier.height(16.dp))
			Text(barcode.value, color = Color.Black, style = MaterialTheme.typography.headlineSmall)
		}
	}
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
	is Activity -> this
	is ContextWrapper -> baseContext.findActivity()
	else -> null
}