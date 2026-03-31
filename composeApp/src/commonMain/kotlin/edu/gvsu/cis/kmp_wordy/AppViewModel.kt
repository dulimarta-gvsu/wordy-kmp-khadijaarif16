package edu.gvsu.cis.kmp_wordy

import androidx.collection.mutableFloatIntMapOf
import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import com.hoc081098.kmp.viewmodel.wrapper.NonNullStateFlowWrapper
import com.hoc081098.kmp.viewmodel.wrapper.wrap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hoc081098.kmp.viewmodel.wrapper.NullableStateFlowWrapper

//replaced
data class Letter(val text: Char = '$', val point: Int = 0, val letterMultiplier: Int =1, val wordMultiplier: Int =1)
@Entity
data class GameSession(
    @PrimaryKey(autoGenerate = true) val session_id: Int=0,
    val word: String, val points: Int,
                       val numMoves: Int, val time: Long)
data class GameSettings(
    val red : Float=0F,val green : Float=0.8F,val blue : Float=0F,
    val minWordLength: Int=3,
    val maxWordLength: Int=8,
    val stockLetters: Int =10
)
enum class Origin {
    Stock, CenterBox
}
// An extension function for easier debugging
fun List<Letter?>.pretty(): String =
    if (this.isEmpty()) "[]" else
    this.map { it?.text ?: "#" }.joinToString(separator = "-")

class AppViewModel(private val dao: GameSessionDao) : ViewModel() {
    private val _sourceLetters = MutableStateFlow(emptyList<Letter?>())
    val sourceLetters: NonNullStateFlowWrapper<List<Letter?>> =
        _sourceLetters.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .wrap()

    private val _targetLetters = MutableStateFlow(emptyList<Letter?>())
    val targetLetters: NonNullStateFlowWrapper<List<Letter?>> =
        _targetLetters.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .wrap()

    //added

    private val letterPoints = mapOf(
        'A' to 1,
        'E' to 1,
        'I' to 1,
        'O' to 1,
        'U' to 1,
        'L' to 1,
        'N' to 1,
        'S' to 1,
        'T' to 1,
        'R' to 1,
        'D' to 2,
        'G' to 2,
        'B' to 3,
        'C' to 3,
        'M' to 3,
        'P' to 3,
        'F' to 4,
        'H' to 4,
        'V' to 4,
        'W' to 4,
        'Y' to 4,
        'K' to 5,
        'J' to 8,
        'X' to 8,
        'Q' to 10,
        'Z' to 10
    )
    private val dictionary = mutableSetOf<String>()
    private val _wordsFound = MutableStateFlow(0)
    val wordsFound: NonNullStateFlowWrapper<Int> =
        _wordsFound.stateIn(viewModelScope, SharingStarted.Eagerly, 0).wrap()

    //score of the word
    private val _wordScore = MutableStateFlow(0)
    val wordScore: NonNullStateFlowWrapper<Int> =
        _wordScore.stateIn(viewModelScope, SharingStarted.Eagerly, 0).wrap()

    //total overall score
    private val _totalScore = MutableStateFlow(0)
    val totalScore: NonNullStateFlowWrapper<Int> =
        _totalScore.stateIn(viewModelScope, SharingStarted.Eagerly, 0).wrap()

    //to record game history
    private val _gameHistory = MutableStateFlow(emptyList<GameSession>())
    val gameHistory: NonNullStateFlowWrapper<List<GameSession>> =
        _gameHistory.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).wrap()
    private val _settings = MutableStateFlow(GameSettings())
    val settings: NonNullStateFlowWrapper<GameSettings> =
        _settings.stateIn(viewModelScope, SharingStarted.Eagerly, GameSettings()).wrap()

    private val _matchNotFound = MutableStateFlow(false)
    val matchNotFound: NonNullStateFlowWrapper<Boolean> =
        _matchNotFound.stateIn(viewModelScope, SharingStarted.Eagerly, false).wrap()
    private var moveCounter = 0
    //new
    private val _dictionary = MutableStateFlow(emptySet<String>())
    private val _isLoadingDictionary = MutableStateFlow(true)
    val isLoadingDictionary: NonNullStateFlowWrapper<Boolean> =
        _isLoadingDictionary.stateIn(viewModelScope, SharingStarted.Eagerly, true).wrap()
    private val _wordMeaning = MutableStateFlow("")
    val wordMeaning: NonNullStateFlowWrapper<String> =
        _wordMeaning.stateIn(viewModelScope, SharingStarted.Eagerly, "").wrap()
    init {
        selectRandomLetters()
        viewModelScope.launch(Dispatchers.IO){
            _gameHistory.update{dao.selectAll()}
        }
        viewModelScope.launch(Dispatchers.IO){
            try{
                val words = WordsfromQuotes()
                _dictionary.update{words}
                println("Dictionary loaded: ${words.size} words were fetched from the API")
            }
            catch(e:Exception){
                println("Failed to fetch: ${e.message}")
            }
            _isLoadingDictionary.value = false
            _matchNotFound.value = false
        }
    }

    //added
    fun settingsApply(newSettings: GameSettings){
        _settings.update { newSettings }
    }
//    fun createDictionary(lines: Sequence<String>) {
//        dictionary.addAll(lines.map { it.trim().uppercase() })
//    }

    //copied from previous assignment
    fun selectRandomLetters() {
        _sourceLetters.update {
            // 60% vowels, 40% consonants
            val vowels = (1..6).map {
                "AEIOU".random()
            }
            val consontants = (1..4).map {
                "BCFGHJKLMNPQRSTVWXYZ".random()
            }
            (vowels + consontants).map { char ->
                val score = letterPoints[char] ?: 0
                //gets score from the scores we initialized, if not found then score is 0
                //pick 100 random numbers, if 1-5 then letter multiplier gets 2, if 6-10 then wordmultiplier does
                val rand = (1..100).random()
                val letter = if (rand in 1..5) 2 else 1
                val word = if (rand in 6..10) 2 else 1
                Letter(char, score, letter, word)
            }.shuffled()
        }
        _targetLetters.update { emptyList() }
        _wordScore.value = 0
        _totalScore.value = 0
        _wordsFound.value = 0
        moveCounter = 0 //reset for new game
        _wordMeaning.value = ""//reset
    }

    fun ReshuffleRemaining() {
        _sourceLetters.update { list -> list.filterNotNull().shuffled() }
    }

    fun calScore(letters: List<Letter>) {
        var score = 0
        var wMult = 1 //by default
        letters.forEach {
            score = score + it.point * it.letterMultiplier
            if (it.wordMultiplier > 1) {
                wMult = wMult * it.wordMultiplier
            }

        }
        _wordScore.value = score * wMult
    }

    fun submitWord(): Boolean {
        val word = _targetLetters.value.filterNotNull().map { it.text }.joinToString("").uppercase()
        println("submitWord called with: $word")
        println("Dictionary size: ${_dictionary.value.size}")
        println("Contains word: ${_dictionary.value.contains(word)}")
        if (_dictionary.value.contains(word)) {
            //sesion an dthen add to history
            _matchNotFound.value = false
            val session = GameSession(
                word = word,
                points = _wordScore.value,
                numMoves = moveCounter,
                time = 0L
            )
            val currentWordScore = _wordScore.value
            _wordMeaning.value = "" //clear the word
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    dao.insert(session)
                    _gameHistory.update { dao.selectAll() }
                    _totalScore.update { it + currentWordScore }
                    _wordsFound.update { it + 1 }


//                    _targetLetters.value = emptyList()
//                    _wordScore.value = 0
//                    moveCounter = 0
                }
                catch(e: Exception){println("Log: Error during submission: ${e.message}")}
                _targetLetters.value = emptyList()
                _wordScore.value = 0
                moveCounter = 0
                _wordMeaning.value = fetchMeaning(word) ?: ""
            }

            //_gameHistory.update { it + session } //added to history
            //have to add scoring
//            _totalScore.update { it + _wordScore.value }
//            _wordsFound.update { it + 1 }
            //clear for next turn
//            _targetLetters.value = emptyList()
//            _wordScore.value = 0
//            moveCounter = 0//reset
            return true
        } else {
            _wordScore.value = 0
            _wordMeaning.value = ""
            _matchNotFound.value = true
        }
        return false

    }
    fun deleteSession(session: GameSession){
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(session)
            _gameHistory.update { dao.selectAll() }
        }
    }
    // This function is needed for the iOS version of the ViewModel
    fun moveTo(group: Origin, itemIndex: Int) {
        if (itemIndex < 0) return
        when (group) {
            Origin.Stock -> {
                if (itemIndex >= _targetLetters.value.size) return
                val letter = _targetLetters.value[itemIndex]
                _sourceLetters.update {
                    it + letter
                }
                _targetLetters.update {
                    it.filterIndexed { index, letter -> index != itemIndex }
                }
                moveCounter++
                calScore(_targetLetters.value.filterNotNull())//added
            }

            Origin.CenterBox -> {
                if (itemIndex >= _sourceLetters.value.size) return
                val letter = _sourceLetters.value[itemIndex]
                _targetLetters.update {
                    it + letter
                }
                _sourceLetters.update {
                    it.filterIndexed { index, letter -> index != itemIndex }
                }
                moveCounter++
                calScore(_targetLetters.value.filterNotNull())//added
            }
        }
    }

    fun rearrangeLetters(group: Origin, arr: List<Letter>) {
        when (group) {
            Origin.Stock -> {
                _sourceLetters.update {
                    arr
                }
            }

            Origin.CenterBox -> {
                _targetLetters.update { arr }
                calScore(arr) //added
                moveCounter++
            }
        }
    }

    //sorting functions
    fun sortbyPoints() {
        //_gameHistory.update { it.sortedBy { s -> s.points } }
        viewModelScope.launch(Dispatchers.IO){
            _gameHistory.update { dao.selectAllSortedByPoints() }
        }

    }

    fun sortbyLength() {
        //_gameHistory.update { it.sortedBy { s -> s.word.length } }
        viewModelScope.launch(Dispatchers.IO){
            _gameHistory.update { dao.selectAllSortedByLength() }
        }

    }

    fun sortAlphabetically() {
        //_gameHistory.update { it.sortedBy { s -> s.word } }
        viewModelScope.launch(Dispatchers.IO){
            _gameHistory.update { dao.selectAllSortedAlphabetically() }
        }
    }

    fun sortbyMovesAndTime() {
//        _gameHistory.update {
//            it.sortedWith(compareBy<GameSession> { s -> s.time }.thenByDescending { s -> s.numMoves })
//        }
        viewModelScope.launch(Dispatchers.IO){
            _gameHistory.update { dao.selectAllSortedByTimeAndMoves() }
        }

    }
}
