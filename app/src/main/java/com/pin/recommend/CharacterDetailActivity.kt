package com.pin.recommend

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pin.recommend.main.SectionsPagerAdapter
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance

class CharacterDetailActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var backgroundImage: ImageView
    private lateinit var backgroundColor: View
    private lateinit var navigation: BottomNavigationView
    private lateinit var viewPager: ViewPager

    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this).get(CharacterDetailsViewModel::class.java)
    }

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)
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

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(this)
        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        backgroundImage = findViewById(R.id.backgroundImage)
        backgroundColor = findViewById(R.id.backgroundColor)
        toolbar = findViewById(R.id.toolbar)

        val id = intent.getLongExtra(INTENT_CHARACTER, -1)
        detailsVM.id.value = id
        detailsVM.state.observe(this) {
            initializeBackground(it)
            initializeToolbar(it)
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }

                R.id.story -> {
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }

                R.id.pay_save -> {
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }

                R.id.event -> {
                    viewPager.currentItem = 3
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

    override fun onPageSelected(p0: Int) {

        when (p0) {
            0 -> navigation.menu.getItem(0).isChecked = true
            1 -> navigation.menu.getItem(1).isChecked = true
            2 -> navigation.menu.getItem(2).isChecked = true
            3 -> navigation.menu.getItem(3).isChecked = true
        }

    }

    override fun onPageScrollStateChanged(p0: Int) {}

    override fun onResume() {
        super.onResume()
        adMobManager.checkAndLoad()
    }

    private fun initializeBackground(state: CharacterDetails.State) {
        state.appearance?.backgroundColor?.let {
            backgroundColor.setBackgroundColor(it)
        }
        state.appearance?.backgroundImageOpacity?.let {
            backgroundColor.alpha = it
        }
        state.appearance.backgroundImage?.let {
            backgroundImage.setImageDrawable(state.appearance?.backgroundImage?.toDrawable(resources))
        }
    }

    private fun initializeToolbar(state: CharacterDetails.State) {
        toolbar.title = state.characterName
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val pinningView = menu.findItem(R.id.fix_home)

        val isPinning = detailsVM.state.value?.isPinning ?: false

        if (isPinning) {
            pinningView.setIcon(R.drawable.pin_fill)
        } else {
            pinningView.setIcon(R.drawable.pin_outline)
        }

        pinningView.setOnMenuItemClickListener {
            if (isPinning) {
                pinningView.setIcon(R.drawable.pin_outline)
                detailsVM.unpinning()
                finish()
            } else {
                pinningView.setIcon(R.drawable.pin_fill)
                detailsVM.pinning()
                Toast.makeText(this@CharacterDetailActivity, "トップページ に固定しました", Toast.LENGTH_SHORT)
                    .show()
            }
            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val isPinning = detailsVM.state.value?.isPinning ?: false

        if (isPinning) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val INTENT_CHARACTER = "com.pin.recommend.CharacterListFragment.INTENT_CHARACTER"
    }
}