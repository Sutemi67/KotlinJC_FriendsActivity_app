package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import apc.appcradle.kotlinjc_friendsactivity_app.R

@Composable
fun AppBackgroundImage() {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                val gradient = Brush.verticalGradient(
                    colorStops = listOf(
                        0.1f to Color.Transparent,
                        0.5f to Color.Black,
                        0.9f to Color.Transparent
                    ).toTypedArray(),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(
                    brush = gradient,
                    blendMode = BlendMode.DstIn
                )
            },
        painter = painterResource(R.drawable.ic_launcher_playstore),
        contentDescription = null,
        alpha = 0.2f,
        contentScale = ContentScale.Crop
    )
}