package apc.appcradle.kotlinjc_friendsactivity_app

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckKoinModulesText : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check Koin modules`() {
        koinPresentationModule.verify()
    }
}