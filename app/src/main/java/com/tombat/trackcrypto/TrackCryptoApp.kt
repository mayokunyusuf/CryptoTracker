package com.tombat.trackcrypto

import android.app.Application
import com.tombat.trackcrypto.crypto.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackCryptoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TrackCryptoApp)
            androidLogger()

            modules(appModule)
        }
    }
}