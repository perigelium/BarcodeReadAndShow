package eu.barcode_1d_improve.ui.restoration

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.barcode_1d_improve.data.BarcodeDetector
import eu.barcode_1d_improve.imaging.BarcodeFrameAnalyzer
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LiveScanScreen(onClose: () -> Unit, viewModel: LiveScanViewModel = koinViewModel()) {
	val context = LocalContext.current
	val resources = LocalResources.current

	val detector = koinInject<BarcodeDetector>()
	val state by viewModel.state.collectAsStateWithLifecycle()
	val scope = rememberCoroutineScope()
	val analyzer = remember {
		BarcodeFrameAnalyzer(
			detector, scope
		                    ) { viewModel.onAction(LiveScanAction.FrameAnalyzed(it)) }
	}

	val haptic = LocalHapticFeedback.current
	LaunchedEffect(state.verified) {
		if (state.verified != null) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
	}

	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		viewModel.effects.collect { effect ->
			when (effect) {
				is LiveScanEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text)
			}
		}
	}

	LaunchedEffect(Unit) { viewModel.onAction(LiveScanAction.Reset) }

	var granted by remember {
		mutableStateOf(
			ContextCompat.checkSelfPermission(
				context,
				Manifest.permission.CAMERA
			                                 ) == PackageManager.PERMISSION_GRANTED
		              )
	}
	val launcher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	                                                ) { granted = it }

	LaunchedEffect(Unit) { if (!granted) launcher.launch(Manifest.permission.CAMERA) }
	BackHandler(onBack = onClose)

	Box(Modifier.fillMaxSize()) {
		if (granted && state.fullScreen == null) {
			CameraPreview(analyzer, Modifier.fillMaxSize())
			LiveOverlay(state.frame, state.verified != null, Modifier.fillMaxSize())
		} else {
			Column(
				Modifier
					.fillMaxSize()
					.padding(24.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally,
			      ) {
				Text("Camera permission is required for live scanning")
				Spacer(Modifier.height(12.dp))
				Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) { Text("Grant permission") }
			}
		}

		TextButton(
			onClick = { viewModel.onAction(LiveScanAction.Reset) },
			modifier = Modifier
				.align(Alignment.TopStart)
				.systemBarsPadding(),
		          ) { Text("Reset") }

		TextButton(
			onClick = onClose,
			modifier = Modifier
				.align(Alignment.TopEnd)
				.systemBarsPadding(),
		          ) { Text("Close") }

		Column(
			Modifier
				.align(Alignment.BottomStart)
				.fillMaxWidth()
				.background(Color.Black.copy(alpha = 0.6f))
				.navigationBarsPadding()
				.padding(12.dp)
		      ) {

			val verified = state.verified
			if (verified != null) {
				Text("Verified ✓  ${verified.read.symbology.label}", color = Color.Green)
				Text(
					verified.read.value,
					color = Color.White,
					style = MaterialTheme.typography.titleLarge
				    )
				Text(
					"${verified.votes} of the last ${verified.framesInWindow} frames agree",
					color = Color.White
				    )

				FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					TextButton(onClick = { viewModel.onAction(LiveScanAction.Reset) }) { Text("Scan again") }
					Button(onClick = {
						val m = resources.displayMetrics
						viewModel.onAction(LiveScanAction.ShowFullScreenClicked(m.widthPixels, m.heightPixels / 3))
					}) { Text("Full screen barcode") }
					Button(
						onClick = { viewModel.onAction(LiveScanAction.SaveClicked) },
						enabled = !state.isSaving,
					      ) { Text("Save image") }
				}
			} else {
				Text(
					"Verifying… ${state.bestVotes}/${LiveScanConfig.REQUIRED} matching frames",
					color = Color.White,
				    )
				LinearProgressIndicator(
					progress = { state.bestVotes.toFloat() / LiveScanConfig.REQUIRED },
					modifier = Modifier.fillMaxWidth(),
				                       )
			}
			Text("Raw reads (debug)", color = Color.White)
			if (state.seen.isEmpty()) Text("No barcode read yet", color = Color.White)
			state.seen.entries.sortedByDescending { it.value }.take(5)
				.forEach { (v, n) -> Text("$v  ×$n", color = Color.White) }
		}

		SnackbarHost(snackbarHostState, Modifier.align(Alignment.TopCenter).systemBarsPadding())

		state.fullScreen?.let { barcode ->
			FullScreenBarcode(barcode) { viewModel.onAction(LiveScanAction.FullScreenDismissed) }
		}
	}
}