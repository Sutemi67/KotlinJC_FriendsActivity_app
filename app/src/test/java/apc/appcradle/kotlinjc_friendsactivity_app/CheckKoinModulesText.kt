package apc.appcradle.kotlinjc_friendsactivity_app

import apc.appcradle.kotlinjc_friendsactivity_app.koin_modules.appModule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckKoinModulesText : KoinTest {
    @Test
    fun `check Koin modules`() {
        appModule.verify()
    }
}