package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models

import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseEvents

interface RatingsEvents : BaseEvents {
    object SyncData : RatingsEvents
}