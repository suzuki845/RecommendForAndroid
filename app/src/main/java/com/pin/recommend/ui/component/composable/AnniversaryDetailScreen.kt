package com.pin.recommend.ui.component.composable

import android.graphics.Picture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pin.recommend.ui.anniversary.AnniversaryScreenShotViewModel
import com.pin.recommend.ui.anniversary.AnniversaryScreenShotViewModelState

@Composable
fun AnniversaryDetailScreen(
    vm: AnniversaryScreenShotViewModel,
    s: AnniversaryScreenShotViewModelState
) {
    val picture = remember { Picture() }
    val state = s.characterDetailsState
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        androidx.compose.ui.graphics.Canvas(
                            picture.beginRecording(
                                width,
                                height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                    vm.setScreenshot(picture)
                }
            }
    ) {
        state.appearance.backgroundImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(state.appearance.backgroundColor)
                )
                .alpha(state.appearance.backgroundImageOpacity)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFFEEEEEE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                state.appearance.iconImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            val fontFamily = state.appearance.typeFace(LocalContext.current)?.let {
                FontFamily(
                    it
                )
            }
            val style = TextStyle(shadow = state.appearance.homeTextShadowColor?.let {
                Shadow(
                    color = Color(it),
                    blurRadius = 3f
                )
            })
            val textColor = Color(state.appearance.homeTextColor)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.characterName,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                Text(
                    text = state.currentAnniversary.topText,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                Text(
                    text = state.currentAnniversary.elapsedDays,
                    fontSize = 34.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                Text(
                    text = state.currentAnniversary.bottomText,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                Text(
                    text = state.currentAnniversary.message,
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )
            }
        }

    }
}