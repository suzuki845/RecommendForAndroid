package com.pin.recommend

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.adapter.AnniversariesDraftAdapter
import com.pin.recommend.adapter.FontAdapter
import com.pin.recommend.databinding.ActivityCreateEventBinding
import com.pin.recommend.databinding.ActivityEditCharacterBinding
import com.pin.recommend.model.viewmodel.AnniversaryEditViewModel
import com.pin.recommend.model.viewmodel.CharacterEditViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance
import com.pin.util.RuntimePermissionUtils
import com.soundcloud.android.crop.Crop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditCharacterActivity : AppCompatActivity() {

    companion object{
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
    }

    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    private val characterVM: CharacterEditViewModel by lazy{
        ViewModelProvider(this).get(CharacterEditViewModel::class.java)
    }

    private val anniversaryVM: AnniversaryEditViewModel by lazy {
        ViewModelProvider(this).get(AnniversaryEditViewModel::class.java)
    }

    private lateinit var binding: ActivityEditCharacterBinding

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup

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

        val id = intent.getLongExtra(INTENT_EDIT_CHARACTER, -1)
        characterVM.initialize(id)

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_edit_character)
        binding.vm = characterVM
        binding.lifecycleOwner = this

        val listView = binding.anniversaries
        val adapter = AnniversariesDraftAdapter(this)
        listView.adapter = adapter
        characterVM.anniversaries.observe(this){
            adapter.setItems(it)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        initializeToolbar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        adMobManager.checkAndLoad()
    }

    fun save(){
    }

    fun onAddAnniversary(v: View){
        val intent = Intent(this, CreateAnniversaryActivity::class.java)
        startActivity(intent)
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
            characterVM.fontFamily.value = adapter.getItem(pos)
            dialog.cancel()
        }

        dialog.show()
    }

    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
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
            characterVM.created.value = date
        }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun initializeToolbar(toolbar: Toolbar) {
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
            val bitmap = BitmapUtility.decodeUri(
                this,
                uri,
                500,
                500
            )
            characterVM.iconImage.value = bitmap
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