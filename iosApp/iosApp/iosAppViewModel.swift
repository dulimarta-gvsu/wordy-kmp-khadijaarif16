import Foundation
import Shared

@MainActor
class iosAppViewModel: ObservableObject {
    private let commonVm: AppViewModel

    @Published var sourceLetters: Array<Letter?> = []
    @Published var targetLetters: Array<Letter?> = []
    //added state flows
    @Published var wordScore: Int =0
    @Published var totalScore: Int =0
    @Published var wordsFound: Int =0
    @Published var gameHistory:Array<GameSession>=[]

    init(commonVm: AppViewModel) {
        self.commonVm = commonVm
        // Monitor changes to the two arrays
        self.commonVm.sourceLetters.subscribe(
            scope: commonVm.viewModelScope,
            onValue: { xxx in
                self.sourceLetters = xxx as? Array<Letter?> ?? []
            })
        self.commonVm.targetLetters.subscribe(
            scope: commonVm.viewModelScope,
            onValue: { xxx in
                self.targetLetters = xxx as? Array<Letter?> ?? []
            })
        // adding for scoring and gameHistory
        self.commonVm.wordScore.subscribe(
        scope: commonVm.viewModelScope,
        onValue: { val in self.wordScore = Int(truncating:val)}
        )
        self.commonVm.totalScore.subscribe(
            scope: commonVm.viewModelScope,
            onValue: { val in self.totalScore = Int(truncating:val)}
            )
        self.commonVm.wordsFound.subscribe(
            scope: commonVm.viewModelScope,
            onValue: { val in self.wordsFound = Int(truncating:val)}
            )
        self.commonVm.gameHistory.subscribe(
            scope: commonVm.viewModelScope,
            onValue: { val in self.gameHistory = val as? Array<GameSession>??[]}
            )
    }

    //add function calls
    func ReshuffleRemaining(){
        self.commonVm.ReshuffleRemaining()
    }
    func submitWord() -> Bool{
            return self.commonVm.submitWord()
        }
    func sortbyPoints(){
            self.commonVm.sortbyPoints()
        }
    func sortbyLength(){
                self.commonVm.sortbyLength()
         }
    func sortAlphabetically(){
                    self.commonVm.sortAlphabetically()
    }
    func sortbyMovesAndTime(){
                        self.commonVm.sortbyMovesAndTime()
        }
    // This is used only on the iOS version
    // The Android version does not need this function
    func moveTo(group: Origin, pos: Int) {
        self.commonVm.moveTo(group: group, itemIndex: Int32(pos))
    }

    func rearrangeLetters(group: Origin, arr: [Letter]) {
        self.commonVm.rearrangeLetters(group: group, arr: arr)
    }

    func selectRandomLetters() {
        self.commonVm.selectRandomLetters()
    }
}
