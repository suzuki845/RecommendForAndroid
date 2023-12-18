package com.pin.recommend

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.CreateAnniversaryActivity.Companion.INTENT_CREATE_ANNIVERSARY
import com.pin.recommend.EditAnniversaryActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.adapter.AnniversariesDraftAdapter
import com.pin.recommend.adapter.FontAdapter
import com.pin.recommend.databinding.ActivityEditCharacterBinding
import com.pin.recommend.dialog.ColorPickerDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.viewmodel.CharacterEditorViewModel
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.Progress
import com.pin.util.*
import com.pin.util.Reward.Companion.getInstance
import com.soundcloud.android.crop.Crop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditCharacterActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
        val REQUEST_CODE_CREATE_ANNIVERSARY = 2983179
        val REQUEST_CODE_EDIT_ANNIVERSARY = 3982432
    }

    private val REQUEST_PICK_ICON = 2000
    private val REQUEST_PICK_BACKGROUND = 2001

    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    private val characterVM: CharacterEditorViewModel by lazy {
        ViewModelProvider(this).get(CharacterEditorViewModel::class.java)
    }

    private var id = -1L

    private lateinit var binding: ActivityEditCharacterBinding
    private lateinit var listView: RecyclerView
    private lateinit var scrollView: ScrollView

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_character)

        adViewContainer = findViewById(R.id.ad_container)

        adMobManager =
            AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id))
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

        id = intent.getLongExtra(INTENT_EDIT_CHARACTER, -1)
        characterVM.initialize(id)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_character)
        binding.vm = characterVM
        binding.lifecycleOwner = this

        binding.imageOpacity.max = 100
        binding.imageOpacity.progress = 100
        binding.imageOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int, fromUser: Boolean
            ) {
                val o = progress * 0.01f
                characterVM.backgroundImageOpacity.value = o
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.previewBackgroundImage.setOnLongClickListener {
            val popup = PopupMenu(this, binding.previewBackgroundImage)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.pic_story_picture_popup, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        characterVM.backgroundImage.value = null
                    }
                }
                false
            }
            popup.show()
            return@setOnLongClickListener true
        }

        binding.previewBackgroundColor.setOnLongClickListener {
            val popup = PopupMenu(this, binding.previewBackgroundColor)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.pic_story_picture_popup, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        characterVM.backgroundColor.value = Color.WHITE
                    }
                }
                false
            }
            popup.show()
            return@setOnLongClickListener true
        }

        binding.previewTextColor.setOnClickListener {
            val dialog = ColorPickerDialogFragment(object :
                DialogActionListener<ColorPickerDialogFragment?> {
                override fun onCancel() {}
                override fun onDecision(dialog: ColorPickerDialogFragment?) {
                    dialog?.let {
                        characterVM.homeTextColor.value = it.color
                    }
                }
            })
            characterVM.homeTextColor.value?.let {
                dialog.setDefaultColor(it)
            }
            dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
        }


        binding.previewTextShadow.setOnClickListener {
            val dialog = ColorPickerDialogFragment(object :
                DialogActionListener<ColorPickerDialogFragment?> {
                override fun onCancel() {}
                override fun onDecision(dialog: ColorPickerDialogFragment?) {
                    dialog?.let {
                        characterVM.homeTextShadowColor.value = it.color
                    }
                }
            })
            characterVM.homeTextShadowColor.value?.let {
                dialog.setDefaultColor(it)
            }
            dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
        }

        scrollView = binding.scrollView

        listView = binding.anniversaries
        val adapter = AnniversariesDraftAdapter(this)
        listView.adapter = adapter
        adapter.setOnItemClickListener {
            val intent = Intent(this, EditAnniversaryActivity::class.java)
            intent.putExtra(INTENT_EDIT_ANNIVERSARY, it.toJson())
            startActivityForResult(intent, REQUEST_CODE_EDIT_ANNIVERSARY)
        }
        characterVM.anniversaries.observeForever {
            adapter.setItems(it)
        }
        listView.layoutManager = LinearLayoutManager(this)

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                characterVM.removeAnniversary(position)
                adapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        initializeToolbar(toolbar)
    }

    private fun initializeToolbar(toolbar: Toolbar) {
        toolbar.title = "編集"
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        adMobManager.checkAndLoad()
    }

    fun save() {
        characterVM.save(Progress({

        }, {
            finish()
        }, { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }))
    }

    fun onAddAnniversary(v: View) {
        if((characterVM.anniversaries.value?.size ?: 0) >= 2){
            Toast.makeText(this, "記念日は2個以上設定できません。", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, CreateAnniversaryActivity::class.java)
        intent.putExtra(CreateAnniversaryActivity.INTENT_CHARACTER_ID, id)
        startActivityForResult(intent, REQUEST_CODE_CREATE_ANNIVERSARY)
    }

    fun onShowFontDialog(view: View?) {
        val adapter = FontAdapter(this)
        val listView = ListView(this)
        listView.adapter = adapter
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this).setTitle("選択してくだい。").setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener { parent, view, pos, id ->
            characterVM.fontFamily.value = adapter.getItem(pos).name
            dialog.cancel()
        }

        dialog.show()
    }

    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog =
            DatePickerDialog(this, OnDateSetListener { dialog, year, month, dayOfMonth ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && !dialog.isShown) {
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


    fun onSetIcon(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_ICON)
    }

    fun onSetBackground(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_BACKGROUND)
    }

    fun onSetBackgroundColor(v: View?) {
        val dialog =
            ColorPickerDialogFragment(object :
                DialogActionListener<ColorPickerDialogFragment> {
                override fun onDecision(dialog: ColorPickerDialogFragment) {
                    characterVM.backgroundColor.value = dialog.color
                }

                override fun onCancel() {}
            })
        characterVM.backgroundColor.value?.let {
            dialog.setDefaultColor(it)
        }
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    private
    var pickMode = 0
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        result: Intent?
    ) {
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            result?.let { beginCropIcon(it.data) }
            pickMode = REQUEST_PICK_ICON
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_ICON) {
            result?.let { handleCropIcon(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_PICK_BACKGROUND && resultCode == RESULT_OK) {
            result?.let { beginCropBackground(it.data) }
            pickMode = REQUEST_PICK_BACKGROUND
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_BACKGROUND) {
            result?.let { handleCropBackground(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_CODE_CREATE_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_CREATE_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    characterVM.addAnniversary(anniversary)
                    scrollView.post{
                        scrollView.fullScroll(View.FOCUS_DOWN)
                    }
                    binding.root.requestFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                }
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_EDIT_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    characterVM.replaceAnniversary(anniversary)
                }
            }
        }

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
                this, uri, 500, 500
            )
            characterVM.iconImage.value = bitmap
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun beginCropBackground(source: Uri?) {
        val destination = Uri.fromFile(File(this.cacheDir, "cropped"))
        val displaySize = DisplaySizeCheck.getDisplaySize(this)
        Crop.of(source, destination).withAspect(displaySize.x, displaySize.y)
            .start(this)
    }

    private fun handleCropBackground(resultCode: Int, result: Intent) {
        println("test!!! handleCropBackground")
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            val bitmap = BitmapUtility.decodeUri(this, uri)
            characterVM.backgroundImage.value = bitmap
            println("test!!! handleCropBackground")
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}