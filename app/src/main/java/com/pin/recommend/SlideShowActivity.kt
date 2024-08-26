package com.pin.recommend

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.daimajia.slider.library.SliderLayout
import com.pin.recommend.model.entity.StoryWithPictures
import com.pin.recommend.view.SlideShowItemView
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance

class SlideShowActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var sliderView: SliderLayout
    private lateinit var story: StoryWithPictures
    private var initPosition = 0
    private var adMobManager: AdMobAdaptiveBannerManager? = null
    private var adViewContainer: ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_show)
        adViewContainer = findViewById(R.id.ad_container)
        adMobManager =
            AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.banner_id))
        adMobManager!!.setAllowAdClickLimit(6)
        adMobManager!!.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager!!.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = getInstance(this)
        reward.isBetweenRewardTime.observe(this) { isBetweenRewardTime ->
            adMobManager!!.setEnable(!isBetweenRewardTime!!)
            adMobManager!!.checkFirst()
        }
        toolbar = findViewById(R.id.toolbar)

        val json = intent.getStringExtra(StoryDetailActivity.INTENT_SLIDE_SHOW) ?: ""
        story = StoryWithPictures.fromJson(json)
        initPosition = intent.getIntExtra(StoryDetailActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION, 0)

        sliderView = findViewById(R.id.slider)
        sliderView.stopAutoCycle()

        for (picture in story.pictures) {
            val bitmap = picture.getBitmap(this@SlideShowActivity, 500, 500)
            val sliderItem = SlideShowItemView(
                this@SlideShowActivity, bitmap
            )
            sliderView.addSlider(sliderItem)
        }
        sliderView.currentPosition = initPosition


        toolbar.title = "スライドショー"
        setSupportActionBar(toolbar)
    }


    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }
}