package com.pin.recommend.ui.gacha

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.insertImage
import com.pin.recommend.BadgeGachaRemoveAdReward
import com.pin.recommend.BadgeGachaUserDidEarnRewardCounter
import com.pin.recommend.R
import com.pin.recommend.databinding.ActivityBadgeGachaBinding
import com.pin.recommend.domain.model.CharacterDetails
import com.pin.recommend.ui.main.SpecialContentsFragment
import com.pin.util.SimpleDialogFragment
import com.pin.util.admob.RewardAdStateAction
import com.pin.util.admob.reward.Reward
import com.pin.util.admob.reward.RewardDialogFragment
import com.pin.util.admob.reward.UserDidEarnRewardCounter

class GachaBadgeActivity : AppCompatActivity() {

    private val vm: GachaBadgeViewModel by lazy {
        ViewModelProvider(this).get(GachaBadgeViewModel::class.java)
    }

    private val binding: ActivityBadgeGachaBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_badge_gacha)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(SpecialContentsFragment.INTENT_SPECIAL_CONTENT_ID) ?: ""

        val json = intent.getStringExtra(SpecialContentsFragment.INTENT_CHARACTER_STATE) ?: "";
        val state = CharacterDetails.State.fromJson(json)
        vm.setCharacterDetailsState(state)
        vm.characterId.value = state.characterId

        binding.lifecycleOwner = this
        binding.gachaVM = vm
        binding.state = state

        binding.roleGachaButton.setOnClickListener {
            onRollGacha()
        }

        val icon = state.appearance.iconImage ?: BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_person_300dp
        )

        vm.summary.observe(this) {
            println("GachaMachineSummary: actual: $it")
            val list = mutableListOf<Bitmap>()
            for (i in 1..it) {
                list.add(icon)
            }
            println("GachaMachineSummary: listSize:${list.size}")
            binding.toteBagView.badges = list
        }

        binding.toolbar.title = "ガチャ"
        setSupportActionBar(binding.toolbar)
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


    fun onSaveImage(view: View) {
        try {
            println("onSaveImage")
            val image = getViewBitmap()
            save(
                this,
                image!!,
                Bitmap.CompressFormat.PNG,
                "image/png",
                "anniversary-${System.currentTimeMillis()}"
            )
            Toast.makeText(
                this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            println(e)
            Toast.makeText(this, "保存に失敗しました。 \n\n ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getViewBitmap(): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            binding.backgroundImage.width, binding.backgroundImage.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        val image = binding.backgroundImage.drawToBitmap(Bitmap.Config.ARGB_8888)
        val imagePaint = Paint().apply {
            alpha = (binding.backgroundImage.alpha * 255).toInt()
        }
        canvas.drawBitmap(image, 0F, 0F, imagePaint)

        val color = binding.backgroundColor.drawToBitmap(Bitmap.Config.ARGB_8888)
        val colorPaint = Paint().apply {
            alpha = (binding.backgroundColor.alpha * 255).toInt()
        }

        canvas.drawBitmap(color, 0F, 0F, colorPaint)

        binding.resultContainer.draw(canvas)

        return bitmap
    }

    fun save(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return insertImage(bitmap, format, mimeType, displayName)
    }

}