import Foundation
import Shared

@MainActor
class iosAppViewModel: ObservableObject {
    private let commonVm: AppViewModel

    @Published var sourceLetters: Array<Letter?> = []
    @Published var targetLetters: Array<Letter?> = []

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
