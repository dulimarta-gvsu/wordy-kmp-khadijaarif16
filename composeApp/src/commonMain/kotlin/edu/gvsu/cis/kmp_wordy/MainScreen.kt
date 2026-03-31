package edu.gvsu.cis.kmp_wordy


import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onVisibilityChangedNode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: AppViewModel, onNavigateToSettings: () -> Unit, onNavigateToHistory: () -> Unit){
//    val stockLetters by viewModel.sourceLetters.collectAsState()
//    val arrangedLetters by viewModel.targetLetters.collectAsState()
    val totalscore by viewModel.totalScore.collectAsState()
    val wordScore by viewModel.wordScore.collectAsState()
    val wordsFound by viewModel.wordsFound.collectAsState()
    //for feedback when word is submitted
    val isLoading by viewModel.isLoadingDictionary.collectAsState()
    val wordMeaning by viewModel.wordMeaning.collectAsState()
    val matchNotFound by viewModel.matchNotFound.collectAsState()
    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement= Arrangement.spacedBy(8.dp)
    ) {
        if(isLoading){
            Text("Loading dictionary...", style = MaterialTheme.typography.bodySmall)
        }
        //need a row of buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

        ){

            Button(
                onClick = {
                    viewModel.ReshuffleRemaining()
                },
            ) {
                Text("Reshuffle")
            }
            Button(
                enabled = wordScore>0,
                onClick = {
                    viewModel.submitWord()
                }

            ) {
                Text("Record Word")
            }
        }
        Text("Word Score: $wordScore")
        if (matchNotFound) {
            Text(
                text = "Match not found!",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (wordMeaning.isNotEmpty()){
            Text(text = "Meaning: $wordMeaning",
            style = MaterialTheme.typography.bodySmall)
        }

        Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween)
        {
            Button(onClick = onNavigateToSettings) { Text("Settings") }
            Button(onClick = { viewModel.selectRandomLetters() }) { Text("New Game") }
            Button(onClick = onNavigateToHistory) { Text("History") }
        }
        HorizontalDivider()
        WordScreen(viewModel=viewModel)

    }
}
