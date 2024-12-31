package com.pin.recommend

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.pin.recommend.adapter.PickStoryPictureAdapter
import com.pin.recommend.viewmodel.StoryDetailsViewModel
import com.pin.util.admob.AdMobAdaptiveBannerManager
import com.pin.util.admob.reward.RemoveAdReward

class StoryDetailActivity : AppCompatActivity() {

    private val vm: StoryDetailsViewModel by lazy {
        ViewModelProvider(this).get(StoryDetailsViewModel::class.java)
    }
    private lateinit var viewStoryPictureAdapter: PickStoryPictureAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var comment: TextView
    private lateinit var toolbar: Toolbar

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)
        adViewContainer = findViewById(R.id.ad_container)
        adMobManager = AdMobAdaptiveBannerManager(
            this,
            adViewContainer,
            resources.getString(R.string.banner_id)
        )
        adMobManager.setAllowAdClickLimit(6)
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = RemoveAdReward.getInstance(this)
        reward.isBetweenRewardTime.observe(this) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime!!)
            adMobManager.checkFirst()
        }

        val id = intent.getLongExtra(INTENT_STORY, -1)
        vm.id.value = id

        recyclerView = findViewById(R.id.recycler_view)
        viewStoryPictureAdapter = PickStoryPictureAdapter(this)
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP
        flexboxLayoutManager.justifyContent = JustifyContent.CENTER
        flexboxLayoutManager.alignItems = AlignItems.FLEX_START
        recyclerView.setLayoutManager(flexboxLayoutManager)
        recyclerView.setAdapter(viewStoryPictureAdapter)
        viewStoryPictureAdapter.setOnClickListener { position ->
            val intent = Intent(this@StoryDetailActivity, SlideShowActivity::class.java)
            intent.putExtra(INTENT_SLIDE_SHOW, vm.story.value?.toJson())
            intent.putExtra(INTENT_SLIDE_SHOW_CURRENT_POSITION, position)
            startActivity(intent)
        }

        vm.story.observe(this) { it ->
            print("id!!!!!${it}")
            comment = findViewById(R.id.comment)
            comment.text = it?.story?.comment
            val images = it?.pictures?.mapNotNull { it.getBitmap(this, 500, 500) }?.toMutableList()
            viewStoryPictureAdapter.setList(images ?: mutableListOf())
        }

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "ストーリー"
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_mode, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                val intent = Intent(this@StoryDetailActivity, EditStoryActivity::class.java)
                intent.putExtra(INTENT_EDIT_STORY, vm.story.value?.toJson())
                startActivity(intent)
            }
        }
        return true
    }

    companion object {
        const val INTENT_STORY = "com.pin.recommend.StoryDetailActivity.INTENT_STORY"
        const val INTENT_SLIDE_SHOW = "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW"
        const val INTENT_SLIDE_SHOW_CURRENT_POSITION =
            "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION"
        const val INTENT_EDIT_STORY = "com.pin.recommend.StoryFragment.INTENT_EDIT_STORY"
    }
}