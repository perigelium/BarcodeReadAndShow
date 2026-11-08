package eu.barcode_1d_improve.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import eu.barcode_1d_improve.common.collectWithLifecycle
import eu.barcode_1d_improve.ui.main.MainContract.UiAction
import eu.barcode_1d_improve.ui.main.MainContract.UiEffect
import eu.barcode_1d_improve.ui.main.MainContract.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core

@Composable
fun MainScreen(
	uiState: UiState,
	uiEffect: Flow<UiEffect>,
	onAction: (UiAction) -> Unit,
              ) {
	uiEffect.collectWithLifecycle {}

	MainContent(
		modifier = Modifier.fillMaxSize(),
		uiState = uiState,
		onAction = onAction,
	           )
}

@Composable
fun MainContent(
	modifier: Modifier = Modifier,
	uiState: UiState,
	onAction: (UiAction) -> Unit,
               ) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center,
	   ) {
			Text("OpenCV loaded: ${OpenCVLoader.initLocal()}, version ${Core.VERSION}",
			fontSize = 24.sp,
		    )
	}
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(
	@PreviewParameter(MainScreenPreviewProvider::class) uiState: UiState,
                     ) {
	MainScreen(
		uiState = uiState,
		uiEffect = emptyFlow(),
		onAction = {},
	          )
}