package com.pin.recommend.ui.gacha

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.BadgeGachaRemoveAdReward
import com.pin.recommend.BadgeGachaUserDidEarnRewardCounter
import com.pin.recommend.R
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.component.composable.ToteBagViewComposable
import com.pin.recommend.util.SimpleDialogFragment
import com.pin.recommend.util.admob.ContentResolverUtil
import com.pin.util.admob.RewardAdStateAction
import com.pin.util.admob.reward.Reward
import com.pin.util.admob.reward.RewardDialogFragment
import com.pin.util.admob.reward.UserDidEarnRewardCounter

class GachaBadgeActivity : AppCompatActivity() {

    companion object {
        val INTENT_CHARACTER_STATE =
            "com.pin.recommend.SpecialContentListFragment.INTENT_CHARACTER_STATE"
    }

    private val vm: GachaBadgeViewModel by lazy {
        ViewModelProvider(this)[GachaBadgeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(INTENT_CHARACTER_STATE) ?: "";
        val state = CharacterDetailsViewModelState.fromJson(json)
        vm.setCharacterId(state.character?.id ?: -1)
        vm.setPrizeImage(state.appearance.iconImage)
        vm.setAppearance(state.appearance)
        vm.observe(this)

        setContent {
            Body(
                vm,
                vm.state.collectAsState(GachaBadgeViewModelState()).value
            )
        }
    }

    @Composable
    fun Body(
        vm: GachaBadgeViewModel,
        state: GachaBadgeViewModelState,
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
                AdaptiveBanner(adId = resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Content(vm, state)
            }
        }
    }

    @Composable
    fun ErrorMessage(
        vm: GachaBadgeViewModel,
        state: GachaBadgeViewModelState,
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
        vm: GachaBadgeViewModel,
        state: GachaBadgeViewModelState,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            state.appearance.backgroundImage?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(state.appearance.backgroundColor))
                    .alpha(state.appearance.backgroundImageOpacity)
            )

            if (state.isComplete) {
                ResultContainer(state.resultImage, state.resultMessage, onReset = { vm.reset() })
            } else {
                GachaContainer(state, state.title, state.isRolling, { onRollGacha() })
            }
        }
    }

    @Composable
    fun GachaContainer(
        state: GachaBadgeViewModelState,
        title: String,
        isRolling: Boolean,
        onRollGacha: () -> Unit
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(min = 300.dp, max = 300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = state.appearance.iconImage ?: BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_person_300dp
                    )
                    val badges = mutableListOf<Bitmap>()
                    for (i in 1..state.summary) {
                        badges.add(icon)
                    }

                    ToteBagViewComposable(
                        badges = badges
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isRolling) {
                    CircularProgressIndicator()
                } else {
                    TextButton(onClick = onRollGacha) {
                        Text("広告を見てガチャを回す")
                    }
                }
            }
        }
    }

    @Composable
    fun ResultContainer(
        resultImage: Bitmap?,
        resultMessage: String,
        onReset: () -> Unit,
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(min = 300.dp, max = 300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                resultImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = resultMessage,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onReset) {
                    Text("もう一度ガチャる")
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

    fun onRollGacha() {
        val removeAdReward = BadgeGachaRemoveAdReward.getInstance(this)
        if (removeAdReward.isBetweenRewardTime.value == true) {
            vm.rollGacha()
            return
        }
        val borderCount = 30
        val counter = BadgeGachaUserDidEarnRewardCounter.getInstance(this)
        val count = borderCount - counter.count()
        SimpleDialogFragment(
            title = "広告を視聴してガチャを回す",
            message = "あと${count}回ガチャを回すと24時間バッジガチャの動画広告が非表示になります。\n(⚠️あと${remainingRewardCoolDownElapsedTimeToHours()}時間経過するとカウントがリセットされます。)",
            onPositive = {
                it.dismiss()
                val progress = ProgressDialog(this)
                progress.setCanceledOnTouchOutside(false)
                progress.show()
                val reward = Reward(resources.getString(R.string.badge_gacha_reward_id))
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
                        if (counter.count() >= borderCount) {
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
        bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return ContentResolverUtil.insertImage(this, bitmap, format, mimeType, displayName)
    }

}