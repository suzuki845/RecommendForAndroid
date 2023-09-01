package com.pin.recommend

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
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
import com.pin.recommend.main.StoryListFragment
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.StoryPictureViewModel
import com.pin.recommend.model.viewmodel.StoryViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance
import com.pin.util.RuntimePermissionUtils.hasSelfPermissions
import com.pin.util.RuntimePermissionUtils.shouldShowRequestPermissionRationale
import com.pin.util.RuntimePermissionUtils.showAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class CreateStoryActivity : AppCompatActivity() {
    private var pickImageView: ImageView? = null
    private var pickStoryPictureAdapter: PickStoryPictureAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var character: RecommendCharacter? = null
    private var editCommentView: EditText? = null
    private var createdView: TextView? = null
    private var created = Date()
    private var accountViewModel: AccountViewModel? = null
    private var storyViewModel: StoryViewModel? = null
    private var storyPictureViewModel: StoryPictureViewModel? = null
    private var adMobManager: AdMobAdaptiveBannerManager? = null
    private var adViewContainer: ViewGroup? = null
    private var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)
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
        accountViewModel = MyApplication.getAccountViewModel(this)
        storyViewModel = ViewModelProvider(this).get(StoryViewModel::class.java)
        storyPictureViewModel = ViewModelProvider(this).get(
            StoryPictureViewModel::class.java
        )
        character = intent.getParcelableExtra(StoryListFragment.INTENT_CREATE_STORY)
        createdView = findViewById(R.id.created)
        editCommentView = findViewById(R.id.comment)
        pickImageView = findViewById(R.id.pickImage)
        recyclerView = findViewById(R.id.recycler_view)
        createdView.setText(FORMAT.format(created))
        pickStoryPictureAdapter = PickStoryPictureAdapter(this)
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP
        flexboxLayoutManager.justifyContent = JustifyContent.CENTER
        flexboxLayoutManager.alignItems = AlignItems.FLEX_START
        recyclerView.setLayoutManager(flexboxLayoutManager)
        recyclerView.setAdapter(pickStoryPictureAdapter)
        pickImageView.setOnClickListener(View.OnClickListener { onPickImage(null) })
        toolbar = findViewById(R.id.toolbar)
        accountViewModel.accountLiveData.observe(this) { account -> initializeToolbar(account) }
    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    private fun initializeToolbar(account: Account) {
        setSupportActionBar(toolbar)
    }

    fun onPickImage(v: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasSelfPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    showAlertDialog(
                        this.supportFragmentManager,
                        "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください"
                    )
                    println("onPickImage 1")
                    return
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MyApplication.REQUEST_PICK_IMAGE
                    )
                    println("onPickImage 2")
                    return
                }
            }
        }
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE)
        }
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            var uri: Uri? = null
            if (resultData != null) {
                uri = resultData.data
                val bitmap = BitmapUtility.decodeUri(this, uri, 500, 500)
                val count = pickStoryPictureAdapter!!.itemCount
                if (count >= 3) {
                    Toast.makeText(this@CreateStoryActivity, "３つ以上は選択出来ません", Toast.LENGTH_SHORT)
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
        val story = Story()
        story.characterId = character!!.id
        story.comment = comment
        story.created = created
        storyViewModel!!.insertStoryWithPicture(story) { insertId ->
            val pictures = pickStoryPictureAdapter!!.list
            for (bitmap in pictures) {
                val storyPicture = StoryPicture()
                storyPicture.storyId = insertId
                storyPicture.saveImage(this@CreateStoryActivity, bitmap)
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
        private const val REQUEST_PICK_STORY_PICTURE = 3000
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
    }
}