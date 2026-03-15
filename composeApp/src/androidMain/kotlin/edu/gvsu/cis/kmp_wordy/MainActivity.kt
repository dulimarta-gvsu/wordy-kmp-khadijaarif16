package edu.gvsu.cis.kmp_wordy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
//    private val appViewModel: AppViewModel by viewModels
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        assets.open("dictionary.txt").bufferedReader().useLines {
//        lines -> appViewModel.createDictionary}
        setContent {

            App(onLoadDictionary = {vm -> assets.open("dictionary.txt").bufferedReader().useLines {
                lines-> vm.createDictionary(lines)
            }})
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}