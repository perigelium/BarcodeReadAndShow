package eu.barcode_1d_improve.ui.restoration

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import java.io.File
import androidx.compose.ui.platform.LocalResources
import androidx.core.content.ContextCompat

@Composable
fun RestorationScreen(onOpenLive: () -> Unit, viewModel: RestorationViewModel = koinViewModel()) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current
	var pendingPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
	val canGenerate =
		state.detections[state.selectedStageIndex]?.any { it.value != null && it.format.zxing != null } == true

	LaunchedEffect(Unit) {
		viewModel.effects.collect { effect ->
			when (effect) {
				is RestorationEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text)
			}
		}
	}

	val picker = rememberLauncherForActivityResult(
		ActivityResultContracts.PickVisualMedia()
	                                              ) { uri ->
		uri?.let { viewModel.onAction(RestorationAction.ImageSelected(it)) }
	}

	val camera = rememberLauncherForActivityResult(
		ActivityResultContracts.TakePicture()
	                                              ) { success ->
		val uri = pendingPhotoUri
		if (success && uri != null) viewModel.onAction(RestorationAction.ImageSelected(uri))
	}

	Box(Modifier.fillMaxSize()) {
		Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(12.dp),
			      ) {            // Buttons

				FlowRow(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
				       ) {
					Button(onClick = {
						picker.launch(
							PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
						             )
					}) { Text("Import") }

					val launchCamera = {
						val dir = File(context.cacheDir, "camera").apply { mkdirs() }
						val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
						val uri = FileProvider.getUriForFile(
							context, "${context.packageName}.fileprovider", file
						                                    )
						pendingPhotoUri = uri
						camera.launch(uri)
					}
					val cameraPermission = rememberLauncherForActivityResult(
						ActivityResultContracts.RequestPermission()
					                                                        ) { granted -> if (granted) launchCamera() }

					Button(onClick = {
						val hasPermission = ContextCompat.checkSelfPermission(
							context, Manifest.permission.CAMERA
						                                                     ) == PackageManager.PERMISSION_GRANTED
						if (hasPermission) launchCamera() else cameraPermission.launch(Manifest.permission.CAMERA)
					}) { Text("Camera") }

					if (state.original != null) {
						Button(
							onClick = { viewModel.onAction(RestorationAction.DetectClicked) },
							enabled = !state.isDetecting,
						      ) { Text("Detect") }

						Button(
							onClick = { viewModel.onAction(RestorationAction.RunPipelineClicked) },
							enabled = !state.isProcessing,
						      ) { Text("Run pipeline") }
					}

					if (canGenerate) {
						Button(onClick = { viewModel.onAction(RestorationAction.GenerateClicked) }) {
							Text(
								"Generate"
							    )
						}
					}

					Button(onClick = onOpenLive) { Text("Live scan") }
				}

				Box(
					Modifier
						.fillMaxWidth()
						.height(4.dp)
				   ) {
					if (state.isLoading || state.isDetecting || state.isProcessing) {
						LinearProgressIndicator(Modifier.fillMaxWidth())
					}
				}

				state.original?.let { original ->
					val stageImage = state.stages.getOrNull(state.selectedStageIndex - 1)
					val shown = stageImage?.image ?: original
					val detection = state.detections[state.selectedStageIndex]

					// ✓ found / ✗ not found / empty = detection not run on that stage
					val marker: (Int) -> String = { index ->
						state.detections[index]?.let { if (it.isEmpty()) " ✗" else " ✓" } ?: ""
					}

					// Stage chips
					if (state.stages.isNotEmpty()) {
						LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
							item {
								FilterChip(
									selected = state.selectedStageIndex == 0,
									onClick = { viewModel.onAction(RestorationAction.StageSelected(0)) },
									label = { Text("Original${marker(0)}") },
								          )
							}
							itemsIndexed(state.stages) { i, stage ->
								FilterChip(
									selected = state.selectedStageIndex == i + 1,
									onClick = { viewModel.onAction(RestorationAction.StageSelected(i + 1)) },
									label = { Text("${stage.name}${marker(i + 1)} (${stage.durationMs} ms)") },
								          )
							}
						}
					}

					Text("Showing: ${stageImage?.name ?: "Original"}, ${shown.width} × ${shown.height}")

					// Image with overlay, sized to the image's aspect ratio so the overlay lines up
					Box(
						modifier = Modifier
							.weight(1f)
							.fillMaxWidth(),
						contentAlignment = Alignment.Center,
					   ) {
						Box(Modifier.aspectRatio(shown.width.toFloat() / shown.height)) {
							Image(
								bitmap = shown.asImageBitmap(),
								contentDescription = "Shown image",
								modifier = Modifier.matchParentSize(),
							     )
							detection?.let {
								BarcodeOverlay(it, shown.width, Modifier.matchParentSize())
							}
						}
					}

					// Detection result for the shown stage
					Column(
						Modifier
							.fillMaxWidth()
							.heightIn(min = 56.dp)
					      ) {
						detection?.let { found ->
							if (found.isEmpty()) {
								Text("No barcode found")
							} else {
								found.forEach { Text("${it.format.label}: ${it.value ?: "(no value)"}") }
							}
						}
					}

					state.generated?.let { g ->
						Text("Generated ${g.symbology.label}: ${g.value}  ${if (g.roundTripOk) "round trip ✓" else "round trip ✗"}")
						Image(
							bitmap = g.bitmap.asImageBitmap(),
							contentDescription = "Generated barcode",
							filterQuality = FilterQuality.None,
							modifier = Modifier
								.fillMaxWidth()
								.height(120.dp),
						     )

						val metrics = LocalResources.current.displayMetrics

						Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
							Button(
								onClick = {
									viewModel.onAction(
										RestorationAction.ShowFullScreenClicked(
											metrics.widthPixels, metrics.heightPixels / 3
										                                       )
									                  )
								},
								enabled = g.roundTripOk,
							      ) { Text("Full screen") }

							Button(
								onClick = { viewModel.onAction(RestorationAction.SaveGeneratedClicked) },
								enabled = g.roundTripOk && !state.isSaving,
							      ) { Text("Save to gallery") }
						}
					}
				}
			}
		}
		state.fullScreen?.let { barcode ->
			FullScreenBarcode(barcode) { viewModel.onAction(RestorationAction.FullScreenDismissed) }
		}
	}
}