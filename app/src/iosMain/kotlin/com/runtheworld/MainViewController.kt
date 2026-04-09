package com.runtheworld

import androidx.compose.ui.window.ComposeUIViewController
import com.runtheworld.di.appModules
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    startKoin {
        modules(appModules())
    }
    return ComposeUIViewController {
        App()
    }
}
