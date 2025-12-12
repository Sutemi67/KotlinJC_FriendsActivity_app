package apc.appcradle.kotlinjc_friendsactivity_app.data

import apc.appcradle.kotlinjc_friendsactivity_app.R

enum class Distances(
    val destination: Int,
    val distance: Double,
    val range: ClosedFloatingPointRange<Double>,
) {
    Beer(destination = R.string.destination_pivo, distance = 1.0, range = 0.0..1.0),
    Marathon(destination = R.string.destination_marathon, 42.0, range = 1.0..42.0),
    Gran(destination = R.string.destination_kanyon, 446.0, range = 42.0..446.0),
    LycianWay(destination = R.string.destination_lycian_way, 540.0, 446.0..540.0),
    Gobi(destination = R.string.destination_gobi, 800.0, 540.0..800.0),
    EarthCenter(destination = R.string.destination_earth, 6371.0, 800.0..6371.0),
    Russia(destination = R.string.destination_Russia, 10000.0, 6371.0..10000.0),
    Ocean(destination = R.string.destination_ocean, 19500.0, 10000.0..19500.0),
    AcrossEarth(destination = R.string.destination_equator, distance = 40075.0, 19500.0..40075.0),
    ToTheMoon(destination = R.string.destination_moon, 384400.0, 40075.0..384400.0),
}