package com.pin.recommend

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.insertImage
import com.pin.recommend.databinding.ActivityEditAnniversaryBinding
import com.pin.recommend.databinding.ActivityScreenShotBinding
import com.pin.recommend.model.CharacterDetails
import com.pin.util.Interstitial
import com.pin.util.Reward
import com.pin.util.RuntimePermissionUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class ScreenShotActivity : AppCompatActivity() {

    companion object {
        val INTENT_SCREEN_SHOT = "com.pin.recommend.ScreenShotActivity.INTENT_SCREEN_SHOT"
    }

    private val binding: ActivityScreenShotBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_screen_shot)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra(INTENT_SCREEN_SHOT) ?: "";
        val state = CharacterDetails.State.fromJson(json)

        binding.state = state
        textShadow(state)

        setSupportActionBar(binding.toolbar)
    }

    private fun textShadow(state: CharacterDetails.State) {
        val c = state.appearance.homeTextShadowColor
        c?.let { s ->
            binding.characterName.setShadowLayer(3f, 0f, 0f, s)
            binding.topText.setShadowLayer(3f, 0f, 0f, s)
            binding.bottomText.setShadowLayer(3f, 0f, 0f, s)
            binding.anniversary.setShadowLayer(3f, 0f, 0f, s)
            binding.elapsedTime.setShadowLayer(3f, 0f, 0f, s)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_screen_shot, menu)

        val item = menu.findItem(R.id.save)
        item.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    try {
                        val dialog = ProgressDialog(this)
                        dialog.show()

                        val image = getViewBitmap()
                        val reward = Reward.getInstance(this)
                        if (reward.isBetweenRewardTime.value == false) {
                            Interstitial.loadAndShow(this, {
                                save(
                                    this,
                                    image!!,
                                    Bitmap.CompressFormat.PNG,
                                    "image/png",
                                    "anniversary-${System.currentTimeMillis()}"
                                )
                                dialog.dismiss()
                                finish()
                                Toast.makeText(
                                    this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                                ).show()
                            }, {
                                save(
                                    this,
                                    image!!,
                                    Bitmap.CompressFormat.PNG,
                                    "image/png",
                                    "anniversary-${System.currentTimeMillis()}"
                                )
                                dialog.dismiss()
                                finish()
                                Toast.makeText(
                                    this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                                ).show()
                            })
                        } else {
                            save(
                                this,
                                image!!,
                                Bitmap.CompressFormat.PNG,
                                "image/png",
                                "anniversary-${System.currentTimeMillis()}"
                            )
                            dialog.dismiss()
                            finish()
                            Toast.makeText(
                                this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        println(e)
                        Toast.makeText(this, "保存に失敗しました。 \n\n ${e.message}", Toast.LENGTH_LONG)
                            .show()
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

    private val REQUEST_PERMISSION_CODE = 1234

    fun save(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return insertImage(bitmap, format, mimeType, displayName)
    }

}