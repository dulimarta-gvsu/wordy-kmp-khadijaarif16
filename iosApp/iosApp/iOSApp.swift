import SwiftUI

@main
struct iOSApp: App {
    init() {
            WordScreen_iosKt.factory = IOSWordScreenFactory()
        }
    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: iosAppViewModel(commonVm: AppViewModel()))
        }
    }
}