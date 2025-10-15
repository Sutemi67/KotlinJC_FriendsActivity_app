package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.Application
import apc.appcradle.kotlinjc_friendsactivity_app.di.koinClasses
import apc.appcradle.kotlinjc_friendsactivity_app.di.koinUseCases
import apc.appcradle.kotlinjc_friendsactivity_app.di.koinViewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FriendsActivityApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@FriendsActivityApplication)
            modules(koinViewModels, koinUseCases, koinClasses)
        }
    }
}