package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

sealed interface AppThemes {
    data object Dark : AppThemes
    data object Light : AppThemes
    data object System : AppThemes
}