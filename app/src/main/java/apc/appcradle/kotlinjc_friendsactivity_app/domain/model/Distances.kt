package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

enum class Distances(
    val destination: String,
    val distance: Double,
    val range: ClosedFloatingPointRange<Double>,
) {
    Pivo(destination = "сходить за пивом", distance = 1.0, range = 0.0..1.0),
    Marafon(destination = "прошагать Марафонскую дистанцию", 42.0, range = 1.0..42.0),
    Gran(destination = "пройти Гран Каньон", 446.0, range = 42.0..446.0),
    Likiy(destination = "пройти по Ликийской тропе", 540.0, 446.0..540.0),
    Gobi(destination = "сквозь пустыню Гоби", 800.0, 540.0..800.0),
    EarthCenter(destination = "дойти до центра Земли", 6371.0, 800.0..6371.0),
    Russia(destination = "пересечь Россию", 10000.0, 6371.0..10000.0),
    Ocean(destination = "по дну через Тихий океан", 19500.0, 10000.0..19500.0),
    AcrossEarth(destination = "обогнуть вокруг Земли", distance = 40075.0, 19500.0..40075.0),
    ToTheMoon(destination = "пешком до Луны", 384400.0, 40075.0..384400.0),
}