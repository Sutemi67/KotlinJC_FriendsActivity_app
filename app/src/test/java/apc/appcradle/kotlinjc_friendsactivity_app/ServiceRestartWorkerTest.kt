package apc.appcradle.kotlinjc_friendsactivity_app

import apc.appcradle.kotlinjc_friendsactivity_app.data.SERVICE_RESTART_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.data.createServiceHealthCheckRequest
import apc.appcradle.kotlinjc_friendsactivity_app.data.createServiceRestartRequest
import org.junit.Test

class ServiceRestartWorkerTest {

    @Test
    fun testCreateServiceRestartRequest() {
        val request = createServiceRestartRequest(delayMillis = 1000L)
        assert(request.tags.contains(SERVICE_RESTART_TAG))
    }

    @Test
    fun testCreateServiceHealthCheckRequest() {
        val request = createServiceHealthCheckRequest()
        assert(request.tags.contains("${SERVICE_RESTART_TAG}_health"))
    }

    @Test
    fun testServiceRestartRequestDefaultDelay() {
        val request = createServiceRestartRequest()
        assert(request.tags.contains(SERVICE_RESTART_TAG))
    }

    @Test
    fun testServiceRestartRequestCustomDelay() {
        val request = createServiceRestartRequest(delayMillis = 5000L)
        assert(request.tags.contains(SERVICE_RESTART_TAG))
    }
}