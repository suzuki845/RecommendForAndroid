package com.pin.recommend.ui.anniversary

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import com.pin.recommend.R
import com.pin.recommend.databinding.ActivityScreenShotBinding
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.util.admob.ContentResolverUtil
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import com.pin.util.admob.reward.RemoveAdReward


class AnniversaryScreenShotActivity : AppCompatActivity() {

    companion object {
        val INTENT_SCREEN_SHOT = "com.pin.recommend.ScreenShotActivity.INTENT_SCREEN_SHOT"
    }

    private val binding: ActivityScreenShotBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_screen_shot)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra(INTENT_SCREEN_SHOT) ?: "";
        val state = CharacterDetailsViewModelState.fromJson(json)
        val typeface = state.appearance.typeFace(this)
        binding.state = state
        binding.typeface = typeface
        textShadow(state)

        setSupportActionBar(binding.toolbar)
    }


    private fun textShadow(state: CharacterDetailsViewModelState) {
        val c = state.appearance.homeTextShadowColor
        c?.let { s ->
            binding.characterName.setShadowLayer(3f, 0f, 0f, s)
            binding.topText.setShadowLayer(3f, 0f, 0f, s)
            binding.bottomText.setShadowLayer(3f, 0f, 0f, s)
            binding.anniversary.setShadowLayer(3f, 0f, 0f, s)
            binding.elapsedTime.setShadowLayer(3f, 0f, 0f, s)
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
            saveInner()
            progress.dismiss()
            finish()
        }, {
            saveInner()
            progress.dismiss()
            finish()
        }, {
            saveInner()
            progress.dismiss()
            finish()
        }))
    }

    private fun saveInner() {
        try {
            val image = getViewBitmap()
            save(
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_screen_shot, menu)

        val item = menu.findItem(R.id.save)
        item.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    val reward = RemoveAdReward.getInstance(this)
                    if (reward.isBetweenRewardTime.value == false) {
                        save()
                    } else {
                        saveInner()
                        finish()
                    }
                }
            }
            false
        }
        return true
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

        binding.container.draw(canvas)

        return bitmap
    }

    fun save(
        bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return ContentResolverUtil.insertImage(this, bitmap, format, mimeType, displayName)
    }

}