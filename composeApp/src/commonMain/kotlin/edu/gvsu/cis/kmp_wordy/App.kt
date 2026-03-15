package edu.gvsu.cis.kmp_wordy
//import com.hoc081098.kmp.viewmodel.compose.kmpViewModel
//import com.hoc081098.kmp.viewmodel.viewModelFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect

@Composable
fun App(onLoadDictionary: (AppViewModel) -> Unit = {}) {
    MaterialTheme {
        val vm: AppViewModel = remember { AppViewModel() }
        LaunchedEffect(vm) { onLoadDictionary(vm) }
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main"){
            composable("main")
            {
                MainScreen(
                    viewModel= vm,
                    onNavigateToSettings = {navController.navigate("settings")},
                    onNavigateToHistory = {navController.navigate("history")}
                )
            }
            composable("settings"){
                SettingsScreen(
                    viewModel= vm,
                    onConfirm = {navController.popBackStack()},
                    onCancel = {navController.popBackStack()}
                )

            }
            composable("history")
            {
                HistoryScreen(
                    viewModel= vm,
                    onBack = {navController.popBackStack()}
                )
            }
        }
    }
}