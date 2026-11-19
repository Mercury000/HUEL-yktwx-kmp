import SwiftUI
import MigrationApp

struct ComposeRoot: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@main
struct MigrationApplication: App {
    var body: some Scene {
        WindowGroup { ComposeRoot().ignoresSafeArea() }
    }
}
