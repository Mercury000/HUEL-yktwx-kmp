package com.mercury.yktwx.migration

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
fun MainViewController(): UIViewController {
    var controller: UIViewController? = null
    val result = ComposeUIViewController {
        MigrationApp(onDarkModeChanged = { dark ->
            val style = if (dark) UIUserInterfaceStyle.UIUserInterfaceStyleDark else UIUserInterfaceStyle.UIUserInterfaceStyleLight
            controller?.overrideUserInterfaceStyle = style
            controller?.view?.window?.overrideUserInterfaceStyle = style
        })
    }
    controller = result
    return result
}
