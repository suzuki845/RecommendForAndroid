package com.pin.recommend

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.ImageSaver
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.AnniversaryManager
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.util.FixedInterstitial
import com.pin.util.Reward
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*


class ScreenShotActivity : AppCompatActivity() {

    companion object {
        val INTENT_SCREEN_SHOT = "com.pin.recommend.ScreenShotActivity.INTENT_SCREEN_SHOT"
    }

    private val viewModel: RecommendCharacterViewModel by lazy {
        ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)
    }

    private val accountViewModel: AccountViewModel by lazy {
        MyApplication.getAccountViewModel(this)!!
    }

    private lateinit var character: RecommendCharacter
    private lateinit var toolbar: Toolbar
    private lateinit var containerView: View
    private lateinit var background: ImageView
    private lateinit var iconImageView: CircleImageView
    private lateinit var characterNameView: TextView
    private lateinit var firstText: TextView
    private lateinit var dateView: TextView
    private lateinit var elapsedView: TextView
    private lateinit var anniversaryView: TextView
    private val now = Calendar.getInstance()
    private lateinit var anniversaryManager: AnniversaryManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_shot)

        toolbar = findViewById(R.id.toolbar)
        background = findViewById(R.id.background)
        containerView = findViewById(R.id.content)
        iconImageView = findViewById(R.id.character_icon)
        dateView = findViewById(R.id.created)
        firstText = findViewById(R.id.first_text)
        elapsedView = findViewById(R.id.elapsedTime)
        characterNameView = findViewById(R.id.character_name)
        anniversaryView = findViewById(R.id.anniversary)

        val characterId = intent.getLongExtra(INTENT_SCREEN_SHOT, -1);
        val characterLiveData = viewModel.getCharacter(characterId)
        characterLiveData.observe(this, Observer { character ->
            if (character == null) return@Observer
            this.character = character
            val image = character.getIconImage(this, 500, 500)
            if (image != null) {
                iconImageView.setImageBitmap(image)
            }
            initializeText(character)
            initializeBackground(character)
            initializeToolbar(character)
        })
    }

    private fun initializeText(character: RecommendCharacter) {
        firstText.text = character.getAboveText()
        firstText.setTextColor(character.getHomeTextColor())
        firstText.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        dateView.text = character.getBelowText()
        dateView.setTextColor(character.getHomeTextColor())
        dateView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        elapsedView.setTextColor(character.getHomeTextColor())
        elapsedView.text = character.getDiffDays(now)
        elapsedView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        characterNameView.text = character.name
        characterNameView.setTextColor(character.getHomeTextColor())
        characterNameView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        anniversaryManager = AnniversaryManager(character)
        anniversaryManager.initialize(character)
        anniversaryView.text = anniversaryManager.nextOrIsAnniversary(now.time)
        anniversaryView.setTextColor(character.getHomeTextColor())
        anniversaryView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        try {
            if (character.fontFamily != null && character.fontFamily != "default") {
                val font = Typeface.createFromAsset(assets, "fonts/" + character.fontFamily + ".ttf")
                firstText.typeface = font
                dateView.typeface = font
                elapsedView.typeface = font
                characterNameView.typeface = font
                anniversaryView.typeface = font
            } else {
                firstText.typeface = null
                dateView.typeface = null
                elapsedView.typeface = null
                characterNameView.typeface = null
                anniversaryView.typeface = null
            }
        } catch (e: RuntimeException) {
            println("font missing " + character.fontFamily)
        }
    }

    private fun initializeBackground(character: RecommendCharacter) {
        background.background = character.getBackgroundDrawable(this, 1000, 1000)
        background.alpha = character.backgroundImageOpacity
    }

    private fun accountToolbarBackgroundColor(account: Account?): Int {
        return account?.getToolbarBackgroundColor() ?: Color.parseColor("#eb34ab")
    }

    private fun accountToolbarTextColor(account: Account?): Int {
        return account?.getToolbarTextColor() ?: Color.parseColor("#ffffff")
    }

    private fun initializeToolbar(character: RecommendCharacter) {
        val account = accountViewModel.accountLiveData.value
        toolbar.setBackgroundColor(character.getToolbarBackgroundColor(this, accountToolbarBackgroundColor(account)))
        toolbar.setTitleTextColor(character.getToolbarTextColor(this, accountToolbarTextColor(account)))
        val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
        if (drawable != null) {
            DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, accountToolbarTextColor(account)))
        }
        MyApplication.setupStatusBarColor(this,
            character.getToolbarTextColor(this, accountToolbarTextColor(account)),
            character.getToolbarBackgroundColor(this, accountToolbarBackgroundColor(account)))
        toolbar.title = character.name
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_screen_shot, menu)

        val item = menu.findItem(R.id.save)
        item.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.save -> {
                    try {
                        val image = getViewBitmap()
                        save(this, image!!, Bitmap.CompressFormat.PNG, "image/png", "anniversary-${Date()}");
                        finish()
                        val reward = Reward.getInstance(this)
                        if(reward.isBetweenRewardTime.value == false){
                            FixedInterstitial.show()
                        }
                        Toast.makeText(
                            this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception){
                        Toast.makeText(this, "保存に失敗しました。", Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        }
        return true
    }

    private fun getViewBitmap(): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            background.width, background.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawARGB(255, 255, 255, 255)
        val b = background.drawToBitmap(Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            alpha = (background.alpha * 255).toInt()
        }
        canvas.drawBitmap(b, 0F,0F, paint)
        containerView.draw(canvas)

        return bitmap
    }

    @Throws(IOException::class)
    fun save(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        var uri: Uri? = null
        return runCatching {
            with(context.contentResolver) {
                insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also {
                    uri = it // Keep uri reference so it can be removed on failure
                    openOutputStream(it)?.use { stream ->
                        if (!bitmap.compress(format, 95, stream))
                            throw IOException("Failed to save bitmap.")
                    } ?: throw IOException("Failed to open output stream.")

                } ?: throw IOException("Failed to create new MediaStore record.")
            }
        }.getOrElse {
            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                context.contentResolver.delete(orphanUri, null, null)
            }
            throw it
        }
    }

}