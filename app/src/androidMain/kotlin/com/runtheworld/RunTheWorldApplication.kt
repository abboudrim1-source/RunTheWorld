package com.runtheworld

import android.app.Application
import com.runtheworld.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RunTheWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@RunTheWorldApplication)
            modules(appModules())
        }
    }
}
