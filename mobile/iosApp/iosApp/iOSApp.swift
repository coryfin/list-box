import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        DatabaseProviderKt.initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}