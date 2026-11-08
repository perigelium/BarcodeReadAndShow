package eu.barcode_1d_improve.ui.restoration

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun RestorationScreenOld(viewModel: RestorationViewModel = koinViewModel()) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		viewModel.effects.collect { effect ->
			when (effect) {
				is RestorationEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text)
			}
		}
	}

	Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
		val picker = rememberLauncherForActivityResult(
			ActivityResultContracts.PickVisualMedia()
		                                              ) { uri ->
			uri?.let { viewModel.onAction(RestorationAction.ImageSelected(it)) }
		}

		Column(
			Modifier.fillMaxSize().padding(padding).padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
		      ) {
			Button(onClick = {
				picker.launch(
					PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
				             )
			}) { Text("Import image") }

			if (state.isLoading) CircularProgressIndicator()

/* 			state.original?.let { bitmap ->
				Text("Original: ${bitmap.width} × ${bitmap.height}")
				Image(
					bitmap = bitmap.asImageBitmap(),
					contentDescription = "Original image",
					contentScale = ContentScale.Fit,
					modifier = Modifier.fillMaxWidth().weight(1f),
				     )
			} */

			/* state.original?.let { bitmap ->
				Text("Original: ${bitmap.width} × ${bitmap.height}")

				Button(
					onClick = { viewModel.onAction(RestorationAction.DetectOriginalClicked) },
					enabled = !state.isDetecting,
				      ) { Text("Detect barcode") }

				Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
					Box(Modifier.aspectRatio(bitmap.width.toFloat() / bitmap.height)) {
						Image(
							bitmap = bitmap.asImageBitmap(),
							contentDescription = "Original image",
							modifier = Modifier.matchParentSize(),
						     )
						state.originalDetection?.let {
							BarcodeOverlay(it, bitmap.width, Modifier.matchParentSize())
						}
					}
				}

				state.originalDetection?.let { found ->
					if (found.isEmpty()) Text("No barcode found")
					else found.forEach { Text("${it.format}: ${it.value ?: "(no value)"}") }
				}

				val stageImage = state.stages.getOrNull(state.selectedStageIndex - 1)
				val shown = stageImage?.image ?: original

				Button(
					onClick = { viewModel.onAction(RestorationAction.RunPipelineClicked) },
					enabled = !state.isProcessing,
				      ) { Text("Run pipeline") }

				if (state.stages.isNotEmpty()) {
					LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						item {
							FilterChip(
								selected = state.selectedStageIndex == 0,
								onClick = { viewModel.onAction(RestorationAction.StageSelected(0)) },
								label = { Text("Original") },
							          )
						}
						itemsIndexed(state.stages) { i, stage ->
							FilterChip(
								selected = state.selectedStageIndex == i + 1,
								onClick = { viewModel.onAction(RestorationAction.StageSelected(i + 1)) },
								label = { Text("${stage.name} (${stage.durationMs} ms)") },
							          )
						}
					}
				}

				Text("Showing: ${stageImage?.name ?: "Original"}, ${shown.width} × ${shown.height}")
			} */
			
		}
	}
}