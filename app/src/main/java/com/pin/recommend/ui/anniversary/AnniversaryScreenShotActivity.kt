package com.pin.recommend.ui.anniversary

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.domain.model.CharacterDetailsState
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.component.composable.AnniversaryDetailScreen
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction


class AnniversaryScreenShotActivity : AppCompatActivity() {

    companion object {
        val INTENT_SCREEN_SHOT = "com.pin.recommend.ScreenShotActivity.INTENT_SCREEN_SHOT"
    }

    private val vm by lazy {
        ViewModelProvider(this)[AnniversaryScreenShotViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra(INTENT_SCREEN_SHOT) ?: "";
        val state = CharacterDetailsState.fromJson(json)
        vm.setCharacterDetailsState(state)
        setContent {
            Body(vm, vm.state.collectAsState(AnniversaryScreenShotViewModelState()).value)
        }

    }

    @Composable
    fun Body(vm: AnniversaryScreenShotViewModel, state: AnniversaryScreenShotViewModelState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                    },
                    actions = {
                        TextButton({
                            save()
                        }) {
                            Text("スクリーンショット")
                        }
                    },
                )
            },
            bottomBar = {
                AdaptiveBanner(adId = resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            SaveSuccess(state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                AnniversaryDetailScreen(vm, state)
            }
        }

    }


    @Composable
    fun ErrorMessage(
        vm: AnniversaryScreenShotViewModel,
        state: AnniversaryScreenShotViewModelState
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
    fun SaveSuccess(state: AnniversaryScreenShotViewModelState) {
        if (state.action == AnniversaryScreenShotViewModelAction.Save && state.status == AnniversaryScreenShotViewModelStatus.Success) {
            Toast.makeText(
                this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
            ).show()
        }
    }

    fun save() {
        val ad = Interstitial(resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(this).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(this, InterstitialAdStateAction({
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


}