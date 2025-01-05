package com.pin.recommend.ui.story

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.TextSliderView
import com.pin.recommend.R
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.util.admob.AdMobAdaptiveBannerManager
import com.pin.util.admob.reward.RemoveAdReward


class StorySlideShowActivity : AppCompatActivity() {
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
            AdMobAdaptiveBannerManager(
                this,
                adViewContainer,
                getString(R.string.banner_id)
            )
        adMobManager!!.setAllowAdClickLimit(6)
        adMobManager!!.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager!!.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = RemoveAdReward.getInstance(this)
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
        val requestOptions = RequestOptions()
        for (picture in story.pictures) {
            val textSliderView = TextSliderView(this)
            textSliderView
                .image(picture.getFile(this))
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)
            sliderView.addSlider(textSliderView)
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