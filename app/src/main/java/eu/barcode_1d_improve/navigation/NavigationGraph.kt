package eu.barcode_1d_improve.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import eu.barcode_1d_improve.navigation.Screen.Main
import eu.barcode_1d_improve.ui.main.MainScreen
import eu.barcode_1d_improve.ui.main.MainViewModel
import org.opencv.android.OpenCVLoader

@Composable
fun NavigationGraph(
	navController: NavHostController,
	startDestination: Screen,
	modifier: Modifier = Modifier,
                   ) {
	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination,
	       ) {
		composable<Main> {
			val viewModel: MainViewModel = koinViewModel()
			val uiState by viewModel.uiState.collectAsStateWithLifecycle()
			val uiEffect = viewModel.uiEffect
			MainScreen(
				uiState = uiState, uiEffect = uiEffect, onAction = viewModel::onAction
			          )
		}
	}
}