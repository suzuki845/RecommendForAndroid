package com.pin.recommend

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.adapter.FontAdapter
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance
import com.pin.util.RuntimePermissionUtils
import com.soundcloud.android.crop.Crop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditCharacterActivity : AppCompatActivity() {

    companion object{
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
    }

    private val NOW = Calendar.getInstance()
    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var characterViewModel: RecommendCharacterViewModel

    private lateinit var character: RecommendCharacter

    private var id: Long = 0
    private var draftIcon: Bitmap? = null
    private var draftCreated: Date = Date()
    private var draftElapsedDateFormat = 0
    private var draftFont: String? = null

    private lateinit var iconImageView: CircleImageView
    private lateinit var characterNameView: EditText
    private lateinit var isZeroDayStartView: Switch
    private lateinit var aboveText: EditText
    private lateinit var belowText: EditText
    private lateinit var createdView: TextView
    private lateinit var fontPickerView: TextView

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_character)

        adViewContainer = findViewById(R.id.ad_container)

        adMobManager = AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id))
        adMobManager.setAllowAdClickLimit(6)
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = getInstance(this)
        reward.isBetweenRewardTime.observe(
            this
        ) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime!!)
            adMobManager.checkFirst()
        }

        accountViewModel = MyApplication.getAccountViewModel(this)
        characterViewModel = ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)

        //character = intent.getParcelableExtra(INTENT_EDIT_CHARACTER)!!
        id = intent.getLongExtra(INTENT_EDIT_CHARACTER, -1)
        val characterLiveData = characterViewModel.getCharacter(id)

        iconImageView = findViewById(R.id.character_icon)
        characterNameView = findViewById(R.id.character_name)
        isZeroDayStartView = findViewById(R.id.is_zero_day_start)
        createdView = findViewById(R.id.created)

        aboveText = findViewById(R.id.above_text)
        belowText = findViewById(R.id.below_text)
        fontPickerView = findViewById(R.id.font_picker)
        createdView.setOnClickListener(View.OnClickListener {
            onShowDatePickerDialog(null)
        })

        characterLiveData.observe(this, Observer { character ->
            if (character == null) return@Observer
            this.character = character
            if (character.fontFamily != null) {
                fontPickerView.text = character.fontFamily
            }else{
                fontPickerView.text = "default"
            }

            character.getIconImage(this, 500, 500)?.let {
                draftIcon = it
                iconImageView.setImageBitmap(it)
            }
            characterNameView.setText(character.name)
            draftCreated = character.created
            isZeroDayStartView.isChecked = character.isZeroDayStart
            createdView.text = FORMAT.format(character.created)
            aboveText.setText(character.getAboveText())
            belowText.setText(character.getBelowText())
            draftFont = character.getFontFamily()
            fontPickerView.text = draftFont
            try {
                if (draftFont != null && !draftFont.equals("default")) {
                    val type = Typeface.createFromAsset(assets, "fonts/" + character.fontFamily + ".ttf")
                    fontPickerView.typeface = type
                }
            } catch (e: RuntimeException) {
            }
        })

        toolbar = findViewById<Toolbar>(R.id.toolbar)

        accountViewModel.accountLiveData.observe(this, Observer { account ->
            account?.let {
                initializeToolbar(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        adMobManager.checkAndLoad()
    }

    fun save(){
        character.name = characterNameView.text.toString()
        character.created = draftCreated
        character.isZeroDayStart = isZeroDayStartView.isChecked
        character.belowText = belowText.text.toString()
        character.aboveText = aboveText.text.toString()
        character.elapsedDateFormat = draftElapsedDateFormat
        character.fontFamily = draftFont
        if(draftIcon != null){
            character.saveIconImage(this, draftIcon)
        }
        characterViewModel.update(character)
    }

    fun onShowFontDialog(view: View?){
        val adapter = FontAdapter(this)
        val listView = ListView(this)
        listView.adapter = adapter
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setTitle("選択してくだい。")
                .setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener{ parent, view, pos, id ->
            draftFont = adapter.getItem(pos)
            fontPickerView.text = draftFont
            try {
                if (draftFont != null && !draftFont.equals("default")) {
                    val type = Typeface.createFromAsset(assets, "fonts/" + draftFont + ".ttf")
                    fontPickerView.typeface = type
                }else{
                    fontPickerView.typeface = null
                }
            } catch (e: RuntimeException) {
            }
            dialog.cancel()
        }

        dialog.show()
    }

    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        calendar.time = draftCreated
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this, OnDateSetListener { dialog, year, month, dayOfMonth ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                    !dialog.isShown) {
                return@OnDateSetListener
                //api19はクリックするとonDateSetが２回呼ばれるため
            }
            val newCalender = Calendar.getInstance()
            newCalender[year, month] = dayOfMonth
            val date = newCalender.time
            draftCreated = date
            createdView.setText(FORMAT.format(draftCreated))
        }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun initializeToolbar(account: Account) {
        toolbar.title = "編集"
        setSupportActionBar(toolbar)
    }

    private val REQUEST_PICK_ICON = 2000
    fun onSetIcon(v: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(this.fragmentManager,
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください")
                    return
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MyApplication.REQUEST_PICK_IMAGE)
                    return
                }
            }
        }
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_ICON)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_ICON)
        }
    }


    private var pickMode = 0
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            result?.let{beginCropIcon(it.data)}
            pickMode = REQUEST_PICK_ICON
        } else if (pickMode == REQUEST_PICK_ICON) {
            result?.let { handleCropIcon(resultCode, it) }
            pickMode = 0
        }
        this.intent.putExtra(Constants.PICK_IMAGE, true)
        return super.onActivityResult(requestCode, resultCode, result)
    }

    private fun beginCropIcon(source: Uri?) {
        val destination = Uri.fromFile(File(this.getCacheDir(), "cropped"))
        Crop.of(source, destination).asSquare().start(this);
    }

    private fun handleCropIcon(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            draftIcon = BitmapUtility.decodeUri(this, uri, 500, 500)
            iconImageView.setImageBitmap(draftIcon)
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_edit_character, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                save()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}