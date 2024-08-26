package com.pin.recommend

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.adapter.PickStoryPictureAdapter
import com.pin.recommend.databinding.ActivityCreateStoryBinding
import com.pin.recommend.main.StoryListFragment
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.viewmodel.StoryEditorViewModel
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.Progress
import com.pin.util.AdLoadingProgress
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.LoadThenShowInterstitial
import com.pin.util.OnAdShowed
import com.pin.util.PermissionChecker
import com.pin.util.Reward.Companion.getInstance
import java.text.SimpleDateFormat
import java.util.Calendar

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var pickStoryPictureAdapter: PickStoryPictureAdapter
    private lateinit var recyclerView: RecyclerView

    private val storyEditorVM: StoryEditorViewModel by lazy {
        ViewModelProvider(this).get(StoryEditorViewModel::class.java)
    }
    private lateinit var binding: ActivityCreateStoryBinding
    private var characterId: Long? = null

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private var adViewContainer: ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)
        adViewContainer = findViewById(R.id.ad_container)
        adMobManager =
            AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.banner_id))
        adMobManager.setAllowAdClickLimit(6)
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = getInstance(this)
        reward.isBetweenRewardTime.observe(this) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime!!)
            adMobManager.checkFirst()
        }

        characterId = intent.getLongExtra(StoryListFragment.INTENT_CREATE_STORY, -1)
        storyEditorVM.characterId.value = characterId

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_story)
        binding.vm = storyEditorVM
        binding.lifecycleOwner = this

        recyclerView = findViewById(R.id.recycler_view)
        pickStoryPictureAdapter = PickStoryPictureAdapter(this)
        pickStoryPictureAdapter.setCanDelete(true)
        pickStoryPictureAdapter.setOnRemoveListener {
            storyEditorVM.removePicture(it)
        }

        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP
        flexboxLayoutManager.justifyContent = JustifyContent.CENTER
        flexboxLayoutManager.alignItems = AlignItems.FLEX_START
        recyclerView.setLayoutManager(flexboxLayoutManager)
        recyclerView.setAdapter(pickStoryPictureAdapter)

        initializeToolbar()
    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    fun onPickImage(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE)
    }

    fun onShowDatePickerDialog(v: View?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { dialog, year, month, dayOfMonth ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                    !dialog.isShown
                ) {
                    return@OnDateSetListener
                }
                val newCalender = Calendar.getInstance()
                newCalender[year, month] = dayOfMonth
                val date = newCalender.time
                storyEditorVM.created.value = date
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            var uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                val bitmap = BitmapUtility.decodeUri(this, uri, 500, 500)
                val count = pickStoryPictureAdapter.itemCount
                if (count >= 3) {
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "３つ以上は選択出来ません",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return
                }
                pickStoryPictureAdapter.add(bitmap)
                val picture = StoryPicture.Draft(bitmap)
                storyEditorVM.addPicture(picture)
            }
        }
        intent.putExtra(Constants.PICK_IMAGE, true)
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_story, menu)
        return true
    }

    private fun save() {
        val ad = LoadThenShowInterstitial(resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(this).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(this, AdLoadingProgress({
            progress.show()
        }, {
            progress.dismiss()
        }, {
            progress.dismiss()
            saveInner()
        }), OnAdShowed({
            saveInner()
        }, {
            progress.dismiss()
            saveInner()
        }))
    }

    private fun saveInner() {
        storyEditorVM.save(Progress({
        }, {
            finish()
        }, {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> save()
        }
        return true
    }

    companion object {
        private const val REQUEST_PICK_STORY_PICTURE = 3000
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
    }
}