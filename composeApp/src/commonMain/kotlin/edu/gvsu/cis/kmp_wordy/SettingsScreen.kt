package edu.gvsu.cis.kmp_wordy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel, onConfirm:()->Unit, onCancel: () -> Unit)
{
    val savedSettings by viewModel.settings.collectAsState()
    var red by remember(savedSettings){mutableStateOf<Float>(savedSettings.red)}
    var green by remember(savedSettings){mutableStateOf<Float>(savedSettings.green)}
    var blue by remember(savedSettings){mutableStateOf<Float>(savedSettings.blue)}
    var minLen by remember(savedSettings){mutableStateOf(savedSettings.minWordLength.toFloat())}
    var maxLen by remember(savedSettings){mutableStateOf(savedSettings.maxWordLength.toFloat())}
    var stockLetters by remember(savedSettings){mutableStateOf(savedSettings.stockLetters.toFloat())}
    var workLengthRange by remember(savedSettings){mutableStateOf(savedSettings.minWordLength.toFloat()..savedSettings.maxWordLength.toFloat())}
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        Text("Setting")
        Text("Red: ${(red*255).toInt()}")
        Slider(value = red, onValueChange = {red=it}, valueRange = 0f..1f)
        Text("Green: ${(green*255).toInt()}")
        Slider(value = green, onValueChange = {green=it}, valueRange = 0f..1f)
        Text("Blue: ${(blue*255).toInt()}")
        Slider(value = blue, onValueChange = {blue=it}, valueRange = 0f..1f)

        Text("Word Length: ${workLengthRange.start.toInt()} - ${workLengthRange.endInclusive.toInt()}")
        RangeSlider(
            value = workLengthRange, onValueChange = {
                workLengthRange = it
                if (stockLetters <it.endInclusive) stockLetters = it.endInclusive
            },
            valueRange = 2f..12f
        )



        Text("Stock Letters: ${(stockLetters).toInt().coerceAtLeast((workLengthRange.endInclusive.toInt()))}")
        Slider(value = stockLetters, onValueChange = {
            stockLetters=it.coerceAtLeast(workLengthRange.endInclusive)
                                                     }, valueRange = 4f..20f)

        //buttons
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth())
        {
            Button(onClick = onCancel) { Text("Cancel") }
            Button(onClick = {
                viewModel.settingsApply(GameSettings(
                    red =red, green =green, blue =blue,
                    minWordLength = workLengthRange.start.toInt(),
                    maxWordLength = workLengthRange.endInclusive.toInt(),
                    stockLetters=stockLetters.toInt().coerceAtLeast(workLengthRange.endInclusive.toInt())
                ))
                onConfirm()
            }) {Text("Confirm")}
        }
    }
}
