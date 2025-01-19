package com.pin.recommend.ui.gacha

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.domain.model.gacha.GachaItemAssetsRepository
import com.pin.recommend.domain.model.gacha.PlaceholderParser
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import com.pin.recommend.ui.main.SpecialContentListFragment
import com.pin.recommend.util.SimpleDialogFragment
import com.pin.recommend.util.admob.ContentResolverUtil
import com.pin.util.admob.RewardAdStateAction
import com.pin.util.admob.reward.RemoveAdReward
import com.pin.util.admob.reward.Reward
import com.pin.util.admob.reward.RewardDialogFragment
import com.pin.util.admob.reward.UserDidEarnRewardCounter

class GachaStringContentActivity : AppCompatActivity() {

    private val vm: GachaStringContentViewModel by lazy {
        ViewModelProvider(this)[GachaStringContentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(SpecialContentListFragment.INTENT_SPECIAL_CONTENT_ID) ?: ""
        val asset = GachaItemAssetsRepository().fetch(id)
        vm.setAsset(asset!!)

        val json = intent.getStringExtra(SpecialContentListFragment.INTENT_CHARACTER_STATE) ?: "";
        val state = CharacterDetailsViewModelState.fromJson(json)
        vm.setAppearance(state.appearance)
        vm.setCharacterName(state.characterName)

        val template = intent.getStringExtra(SpecialContentListFragment.INTENT_PLACE_HOLDER) ?: ""
        vm.setPlaceHolder(PlaceholderParser(template))

        vm.observe(this)

        setContent {
            Body(
                vm,
                vm.state.collectAsState(GachaStringContentViewModelState()).value
            )
        }
    }

    @Composable
    fun Body(
        vm: GachaStringContentViewModel,
        state: GachaStringContentViewModelState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text("ガチャ")
                    },
                )
            },
            bottomBar = {
                ComposableAdaptiveBanner(adId = resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Content(
                    state = state,
                    onRollGacha = {
                        onRollGacha(null)
                    },
                    onReset = {
                        vm.reset()
                    })
            }
        }
    }

    @Composable
    fun ErrorMessage(
        vm: GachaStringContentViewModel,
        state: GachaStringContentViewModelState
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
        state: GachaStringContentViewModelState,
        onRollGacha: () -> Unit,
        onReset: () -> Unit
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            state.appearance.backgroundImage?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Background Color Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(state.appearance.backgroundColor))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Container
                if (!state.isComplete) {
                    Card(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(8.dp)),
                        elevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .widthIn(min = 180.dp, max = 200.dp)
                                .heightIn(min = 120.dp, max = 160.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.title,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (state.isRolling) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                TextButton(onClick = onRollGacha) {
                                    Text("広告を見てガチャを回す")
                                }
                            }
                        }
                    }
                }

                // Result Container
                if (state.isComplete) {
                    Card(
                        modifier = Modifier.size(350.dp),
                        elevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            state.appearance.iconImage?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(220.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Text(
                                text = state.result().toString(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // One More and Save Image Buttons
                    Card(
                        modifier = Modifier.wrapContentSize(),
                        elevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextButton(onClick = onReset) {
                                Text("もう一度ガチャる")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun remainingRewardCoolDownElapsedTimeToHours(): Int {
        val counter = UserDidEarnRewardCounter(this)
        val v = counter.remainingRewardCoolDownElapsedTimeToHours()
        if (v <= 0) {
            return 16
        }
        return v
    }

    fun onRollGacha(view: View?) {
        val removeAdReward = RemoveAdReward.getInstance(this)

        if (removeAdReward.isBetweenRewardTime.value == true) {
            vm.rollGacha()
            return
        }

        val border = 10
        val counter = UserDidEarnRewardCounter.getInstance(this)
        val count = border - counter.count()

        SimpleDialogFragment(
            title = "広告を視聴してガチャを回す",
            message = "あと${count}回ガチャを回すと24時間バッジガチャ以外の動画広告が非表示になります。\n(⚠️あと${remainingRewardCoolDownElapsedTimeToHours()}時間経過するとカウントがリセットされます。)",
            onPositive = {
                it.dismiss()
                val progress = ProgressDialog(this)
                progress.setCanceledOnTouchOutside(false)
                progress.show()
                val reward = Reward(resources.getString(R.string.reward_id))
                reward.show(this, RewardAdStateAction(
                    onLoading = {},
                    onLoadComplete = {
                        progress.dismiss()
                    },
                    onLoadFailed = { _ ->
                        progress.dismiss()
                        vm.rollGacha()
                    },
                    onShowComplete = {},
                    onShowFailed = { _ ->
                        vm.rollGacha()
                    },
                    onUserEarnedReward = { _ ->
                        counter.increment()
                        vm.rollGacha()
                        if (counter.count() >= border) {
                            removeAdReward.setTimeLeft(24)
                            counter.reset()
                        }
                    }
                ))
            },
            onNegative = { it.dismiss() },
        ).show(supportFragmentManager, RewardDialogFragment.TAG)
    }


    fun save(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return ContentResolverUtil.insertImage(context, bitmap, format, mimeType, displayName)
    }


}