package com.pin.recommend

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.adapter.PickStoryPictureAdapter
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.StoryPictureViewModel
import com.pin.recommend.model.viewmodel.StoryViewModel
import com.pin.recommend.util.PermissionRequests
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.PermissionChecker
import com.pin.util.Reward.Companion.getInstance
import com.pin.util.RuntimePermissionUtils.hasSelfPermissions
import com.pin.util.RuntimePermissionUtils.shouldShowRequestPermissionRationale
import com.pin.util.RuntimePermissionUtils.showAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class EditStoryActivity : AppCompatActivity() {
    private lateinit var pickImageView: ImageView
    private lateinit var editCommentView: EditText
    private lateinit var createdView: TextView
    private lateinit var recyclerView: RecyclerView
    private var pickStoryPictureAdapter: PickStoryPictureAdapter? = null
    private var created = Date()
    private var story: Story? = null
    private var storyViewModel: StoryViewModel? = null
    private var storyPictureViewModel: StoryPictureViewModel? = null
    private var adMobManager: AdMobAdaptiveBannerManager? = null
    private var adViewContainer: ViewGroup? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_story)
        adViewContainer = findViewById(R.id.ad_container)
        adMobManager =
            AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id))
        adMobManager!!.setAllowAdClickLimit(6)
        adMobManager!!.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager!!.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = getInstance(this)
        reward.isBetweenRewardTime.observe(this) { isBetweenRewardTime ->
            adMobManager!!.setEnable(!isBetweenRewardTime!!)
            adMobManager!!.checkFirst()
        }
        storyViewModel = ViewModelProvider(this).get(StoryViewModel::class.java)
        storyPictureViewModel = ViewModelProvider(this).get(
            StoryPictureViewModel::class.java
        )
        story = intent.getParcelableExtra(StoryDetailActivity.INTENT_EDIT_STORY)
        createdView = findViewById(R.id.created)
        pickImageView = findViewById(R.id.pickImage)
        editCommentView = findViewById(R.id.comment)
        recyclerView = findViewById(R.id.recycler_view)
        pickImageView.setOnClickListener(View.OnClickListener { onPickImage(null) })
        pickStoryPictureAdapter = PickStoryPictureAdapter(this)
        pickStoryPictureAdapter!!.setCanDelete(true)
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP
        flexboxLayoutManager.justifyContent = JustifyContent.CENTER
        flexboxLayoutManager.alignItems = AlignItems.FLEX_START
        recyclerView.setLayoutManager(flexboxLayoutManager)
        recyclerView.setAdapter(pickStoryPictureAdapter)
        editCommentView.setText(story!!.comment)
        storyViewModel!!.findByTrackedId(story!!.id).observe(this) { story ->
            editCommentView.setText(story.comment)
            created = story.created
            createdView.setText(FORMAT.format(story.created))
        }
        storyPictureViewModel!!.findByTrackedStoryId(story!!.id).observe(this) { storyPictures ->
            val images: MutableList<Bitmap> = ArrayList()
            for (storyPicture in storyPictures) {
                images.add(storyPicture.getBitmap(this@EditStoryActivity, 500, 500))
            }
            pickStoryPictureAdapter!!.list = images
        }
        toolbar = findViewById(R.id.toolbar)

        initializeToolbar()
    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    private fun initializeToolbar() {
        toolbar!!.title = "ストーリー編集"
        setSupportActionBar(toolbar)
    }

    fun onShowDatePickerDialog(v: View?) {
        val calendar = Calendar.getInstance()
        calendar.time = story!!.created
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
                createdView!!.text = FORMAT.format(date)
                created = date
            },
            year,
            month,
            dayOfMonth
        )
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show()
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            var uri: Uri? = null
            if (resultData != null) {
                uri = resultData.data
                val bitmap = BitmapUtility.decodeUri(this, uri, 500, 500)
                val count = pickStoryPictureAdapter!!.itemCount
                if (count >= 3) {
                    Toast.makeText(this@EditStoryActivity, "３つ以上は選択出来ません", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                pickStoryPictureAdapter!!.add(bitmap)
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
        val comment = editCommentView!!.text.toString()
        story!!.comment = comment
        story!!.created = created
        storyViewModel!!.updateWithPicture(story) { storyId ->
            for (sp in storyPictureViewModel!!.findByStoryId(storyId)) {
                sp.deleteImage(this@EditStoryActivity)
                storyPictureViewModel!!.delete(sp)
            }
            val pictures = pickStoryPictureAdapter!!.list
            for (bitmap in pictures) {
                val storyPicture = StoryPicture()
                storyPicture.storyId = story!!.id
                storyPicture.saveImage(this@EditStoryActivity, bitmap)
                storyPictureViewModel!!.insert(storyPicture)
            }
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> save()
        }
        return true
    }

    companion object {
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
        private const val REQUEST_PICK_STORY_PICTURE = 3000
    }
}