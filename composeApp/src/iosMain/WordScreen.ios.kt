package edu.gvsu.cis.kmp_wordy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import platform.UIKit.UIViewController

// Factory interface
interface WordScreenFactory {
    fun createWordScreen(viewModel: AppViewModel): Any
}

// Global factory instance, set by iOSApp.swift at startup
lateinit var factory: WordScreenFactory

@Composable
actual fun WordScreen(viewModel: AppViewModel) {
    val wordScreenView = remember { factory.createWordScreen(viewModel) as UIViewController }
    UIKitViewController(
        factory = { wordScreenView },
        modifier = Modifier.fillMaxSize()
    )
}