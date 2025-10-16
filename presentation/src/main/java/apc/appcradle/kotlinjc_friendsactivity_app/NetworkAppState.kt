package apc.appcradle.kotlinjc_friendsactivity_app

import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

data class NetworkAppState(

    val isLoading: Boolean = false,
    val isSuccessful: Boolean? = null,
    val errorMessage: String? = null,

    val isLoggedIn: Boolean = false,
    val userLogin: String? = null,
    val isPermissionsGet: Boolean = false,

    //Navigation
    val currentDestination: String = Destinations.AUTH.route,
)