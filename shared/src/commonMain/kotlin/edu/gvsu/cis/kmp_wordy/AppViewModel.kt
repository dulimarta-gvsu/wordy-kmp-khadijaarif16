package edu.gvsu.cis.kmp_wordy

import com.hoc081098.kmp.viewmodel.ViewModel
import com.hoc081098.kmp.viewmodel.wrapper.NonNullStateFlowWrapper
import com.hoc081098.kmp.viewmodel.wrapper.wrap
import edu.gvsu.cis.kmp_wordy.pretty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class Letter(val text: Char = '$', val point: Int = 0)

enum class Origin {
    Stock, CenterBox
}
// An extension function for easier debugging
fun List<Letter?>.pretty(): String =
    if (this.isEmpty()) "[]" else
    this.map { it?.text ?: "#" }.joinToString(separator = "-")

class AppViewModel : ViewModel() {
    private val _sourceLetters = MutableStateFlow(emptyList<Letter?>())
    val sourceLetters: NonNullStateFlowWrapper<List<Letter?>> =
        _sourceLetters.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .wrap()

    private val _targetLetters = MutableStateFlow(emptyList<Letter?>())
    val targetLetters: NonNullStateFlowWrapper<List<Letter?>> =
        _targetLetters.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .wrap()

    init {
        selectRandomLetters()
    }

    fun selectRandomLetters() {
        _sourceLetters.update {
            // 60% vowels, 40% consonants
            val vowels = (1..6).map {
                "AEIOU".random()
            }
            val consontants = (1..4).map {
                "BCFGHJKLMNPQRSTVWXYZ".random()
            }
            (vowels + consontants).map {
                Letter(it)
            }.shuffled()
        }
        _targetLetters.update { emptyList() }
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
                _targetLetters.update {
                    arr
                }
            }
        }
    }
}
