package com.pin.recommend.ui.payment

import android.app.ProgressDialog
import android.graphics.Picture
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
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
import com.pin.recommend.R
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction


@Composable
fun PaymentWholePeriodComponent(
    viewType: Int,
    vm: PaymentWholePeriodViewModel,
    state: PaymentWholePeriodViewModelState
) {
    val activity = LocalContext.current as AppCompatActivity
    val title = if (viewType == 0) "全期間のPayの合計" else "全期間の貯金の合計"
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.Black,
                title = {
                    Text(title)
                },
                actions = {
                    TextButton({
                        save(
                            activity,
                            vm
                        )
                    }) {
                        Text("スクリーンショット")
                    }
                },
            )
        },
    ) { padding ->
        ErrorMessage(vm, state)
        SaveSuccess(state)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Content(viewType, vm, state)
        }
    }
}

@Composable
fun SaveSuccess(
    state: PaymentWholePeriodViewModelState,
) {
    val context = LocalContext.current
    if (state.action == PaymentWholePeriodViewModelAction.Save &&
        state.status == PaymentWholePeriodViewModelStatus.Success
    ) {
        Toast.makeText(
            context, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
        ).show()

    }
}

@Composable
fun ErrorMessage(
    vm: PaymentWholePeriodViewModel,
    state: PaymentWholePeriodViewModelState
) {
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.resetError() },
            title = { Text("Error") },
            text = { Text(state.errorMessage) },
            confirmButton = {
                TextButton(onClick = { vm.resetError() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun Content(
    viewType: Int,
    vm: PaymentWholePeriodViewModel,
    state: PaymentWholePeriodViewModelState
) {
    val picture = remember { Picture() }
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
                val word = if (viewType == 0) "推しにPayして" else "推し貯金して"
                Text(
                    text = word,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                val amount = if (viewType == 0) state.paymentAmount else state.savingsAmount
                Text(
                    text = "${amount}円",
                    fontSize = 34.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )

                Text(
                    text = "になりました",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp),
                    fontFamily = fontFamily,
                    style = style,
                    color = textColor,
                )
            }
        }
    }
}

fun save(
    activity: AppCompatActivity,
    vm: PaymentWholePeriodViewModel,
) {
    val ad = Interstitial(activity.resources.getString(R.string.interstitial_f_id))
    val progress = ProgressDialog(activity).apply {
        setTitle("少々お待ちください...")
        setCancelable(false)
    }
    ad.show(activity, InterstitialAdStateAction({
        progress.show()
    }, {
        progress.dismiss()
    }, {
        vm.saveScreenshot()
        progress.dismiss()
    }, {
        vm.saveScreenshot()
        progress.dismiss()
    }, {
        vm.saveScreenshot()
        progress.dismiss()
    }))
}
