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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.insertImage
import com.pin.recommend.model.entity.AnniversaryManager
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.util.Interstitial
import com.pin.util.Reward
import com.pin.util.RuntimePermissionUtils
import de.hdodenhof.circleimageview.CircleImageView
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
    private lateinit var backgroundImage: View
    private lateinit var backgroundColor: View
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
        backgroundImage = findViewById(R.id.backgroundImage)
        backgroundColor = findViewById(R.id.backgroundColor)
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
        backgroundImage.background = character.getBackgroundImageDrawable(this, 1000, 1000)
        backgroundImage.alpha = character.backgroundImageOpacity

        character.backgroundColor?.let {
            backgroundColor.setBackgroundColor(it)
        }
    }

    private fun initializeToolbar(character: RecommendCharacter) {
        toolbar.title = character.name
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_screen_shot, menu)

        val item = menu.findItem(R.id.save)
        item.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.save -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!RuntimePermissionUtils.hasSelfPermissions(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            if (RuntimePermissionUtils.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                RuntimePermissionUtils.showAlertDialog(
                                    fragmentManager,
                                    "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください"
                                )
                                return@setOnMenuItemClickListener false
                            } else {
                                requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    REQUEST_PERMISSION_CODE
                                )
                                return@setOnMenuItemClickListener false
                            }
                        }
                    }

                    try {
                        val dialog = ProgressDialog(this)
                        dialog.show()

                        val image = getViewBitmap()
                        val reward = Reward.getInstance(this)
                        if(reward.isBetweenRewardTime.value == false){
                            Interstitial.loadAndShow(this, {
                                save(this, image!!, Bitmap.CompressFormat.PNG, "image/png", "anniversary-${System.currentTimeMillis()}")
                                dialog.dismiss()
                                finish()
                                Toast.makeText(
                                    this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                                ).show()
                            }, {
                                save(this, image!!, Bitmap.CompressFormat.PNG, "image/png", "anniversary-${System.currentTimeMillis()}")
                                dialog.dismiss()
                                finish()
                                Toast.makeText(
                                    this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                                ).show()
                            })
                        }else{
                            save(this, image!!, Bitmap.CompressFormat.PNG, "image/png", "anniversary-${System.currentTimeMillis()}")
                            dialog.dismiss()
                            finish()
                            Toast.makeText(
                                this, """
     スクリーンショットを保存しました。ファイルをご確認ください。
     """.trimIndent(), Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception){
                        println(e)
                        Toast.makeText(this, "保存に失敗しました。 \n\n ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        }
        return true
    }

    private fun getViewBitmap(): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            backgroundImage.width, backgroundImage.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        val image = backgroundImage.drawToBitmap(Bitmap.Config.ARGB_8888)
        val imagePaint = Paint().apply {
            alpha = (backgroundImage.alpha * 255).toInt()
        }
        canvas.drawBitmap(image, 0F,0F, imagePaint)

        val color = backgroundColor.drawToBitmap(Bitmap.Config.ARGB_8888)
        val colorPaint = Paint().apply {
            alpha = (backgroundColor.alpha * 255).toInt()
        }

        canvas.drawBitmap(color, 0F, 0F, colorPaint)

        containerView.draw(canvas)

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