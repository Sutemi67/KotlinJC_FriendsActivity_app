package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.Application
import apc.appcradle.kotlinjc_friendsactivity_app.utils.koinAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FriendsActivityApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@FriendsActivityApplication)
            modules(koinAppModule)
        }
    }
}