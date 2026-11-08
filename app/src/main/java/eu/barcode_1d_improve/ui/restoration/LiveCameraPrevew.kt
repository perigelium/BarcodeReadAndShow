package eu.barcode_1d_improve.ui.restoration

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LiveCameraPreview(modifier: Modifier = Modifier) {
	val lifecycleOwner = LocalLifecycleOwner.current
	var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

	// Release the camera when this composable leaves the screen
	DisposableEffect(Unit) { onDispose { cameraProvider?.unbindAll() } }

	AndroidView(
		modifier = modifier,
		factory = { ctx ->
			val previewView = PreviewView(ctx)
			val future = ProcessCameraProvider.getInstance(ctx)
			future.addListener({
				                   val provider = future.get()
				                   cameraProvider = provider
				                   val preview = Preview.Builder().build()
					                   .also { it.setSurfaceProvider(previewView.surfaceProvider) }
				                   runCatching {
					                   provider.unbindAll()
					                   provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
				                   }
			                   }, ContextCompat.getMainExecutor(ctx))
			previewView
		},
	           )
}