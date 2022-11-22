package com.pin.recommend

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pin.recommend.dialog.BackgroundSettingDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.dialog.TextSettingDialogFragment
import com.pin.recommend.dialog.ToolbarSettingDialogFragment
import com.pin.recommend.main.SectionsPagerAdapter
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Interstitial
import com.pin.util.Reward.Companion.getInstance
import kotlinx.android.synthetic.main.activity_character_detail.view.*

class CharacterDetailActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var backgroundImage: View
    private lateinit var backgroundColor: View
    private lateinit var navigation: BottomNavigationView
    private lateinit var viewPager: ViewPager
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var characterViewModel: RecommendCharacterViewModel
    private lateinit var character: RecommendCharacter
    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)
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


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(this)
        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        backgroundImage = findViewById(R.id.backgroundImage)
        backgroundColor = findViewById(R.id.backgroundColor)
        toolbar = findViewById(R.id.toolbar)


        accountViewModel = MyApplication.getAccountViewModel(this)
        characterViewModel = ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)

        character = intent.getParcelableExtra(INTENT_CHARACTER)!!
        val characterLiveData = characterViewModel.getCharacter(character.id)
        characterLiveData.observe(this, Observer { character ->
            if (character == null) return@Observer
            this@CharacterDetailActivity.character = character
            initializeBackground(character)
            initializeToolbar(character)
        })
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

        when(p0) {
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

    private fun initializeBackground(character: RecommendCharacter) {
        backgroundImage.background = character.getBackgroundImageDrawable(this@CharacterDetailActivity, 1000, 1000)
        backgroundImage.alpha = character.backgroundImageOpacity

        character.backgroundColor
            .let {
                backgroundColor.setBackgroundColor(it)
            }
    }

    private fun initializeToolbar(character: RecommendCharacter) {
        toolbar.title = character.name
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val deleteBackgroundImage = menu.findItem(R.id.delete_background_image)
        deleteBackgroundImage.isVisible = character.hasBackgroundImage()
        val account = accountViewModel.accountLiveData.value

        val item = menu.findItem(R.id.fix_home)
        item.setOnMenuItemClickListener { item ->
            account?.let {
                if (it.fixedCharacterId != null) {
                    item.setIcon(R.drawable.pin_outline)
                    it.removeFixedCharacter()
                    accountViewModel.saveAccount(it)
                    finish()
                } else {
                    item.setIcon(R.drawable.pin_fill)
                    it.setFixedCharacter(character.id)
                    accountViewModel.saveAccount(it)
                    Toast.makeText(this@CharacterDetailActivity, "トップページ に固定しました", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
        account?.let {
            if (it.getFixedCharacterId() == null) {
                item.setIcon(R.drawable.pin_outline)
            } else {
                item.setIcon(R.drawable.pin_fill)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_body_text_color -> {
                val bodyTextSettingDialogFragment = TextSettingDialogFragment(object : DialogActionListener<TextSettingDialogFragment> {
                    override fun onDecision(dialog: TextSettingDialogFragment) {
                        character.homeTextColor = dialog.textColor
                        character.homeTextShadowColor = dialog.textShadowColor
                        characterViewModel.update(character)
                    }

                    override fun onCancel() {}
                })
                bodyTextSettingDialogFragment.setDefaultTextColor(character.getHomeTextColor())
                bodyTextSettingDialogFragment.setDefaultTextShadowColor(character.getHomeTextShadowColor())
                bodyTextSettingDialogFragment.show(supportFragmentManager, ToolbarSettingDialogFragment.TAG)
                true
            }
            R.id.change_background_image -> {
                val backgroundSettingDialogFragment = BackgroundSettingDialogFragment(object : DialogActionListener<BackgroundSettingDialogFragment> {
                    override fun onDecision(dialog: BackgroundSettingDialogFragment) {
                        if (dialog.backgroundImage != null) {
                            character.backgroundImageOpacity = dialog.imageOpacity
                            character.saveBackgroundImage(this@CharacterDetailActivity, dialog.backgroundImage)
                        }
                        character.backgroundColor = dialog.backgroundColor
                        characterViewModel.update(character)
                    }

                    override fun onCancel() {}
                })
                backgroundSettingDialogFragment.setDefaultBackgroundImage(character.getBackgroundBitmap(this, 300, 300))
                backgroundSettingDialogFragment.setDefaultBackgroundColor(character.getBackgroundColor())
                backgroundSettingDialogFragment.setDefaultImageOpacity(character.backgroundImageOpacity)
                backgroundSettingDialogFragment.show(supportFragmentManager, BackgroundSettingDialogFragment.TAG)
                true
            }
            R.id.delete_background_image -> {
                character.deleteBackgroundImage(this)
                characterViewModel.update(character)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val account = accountViewModel.accountLiveData.value
        if (account != null) {
            if (account.getFixedCharacterId() != null) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        const val INTENT_CHARACTER = "com.pin.recommend.CharacterListFragment.INTENT_CHARACTER"
    }
}