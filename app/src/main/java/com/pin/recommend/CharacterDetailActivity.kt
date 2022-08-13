package com.pin.recommend

import android.content.res.ColorStateList
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
import com.google.android.material.tabs.TabLayout
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
import com.pin.util.FixedInterstitial
import com.pin.util.Reward.Companion.getInstance

class CharacterDetailActivity : AppCompatActivity() {
    private lateinit var background: View
    private lateinit var tabs: TabLayout
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


        FixedInterstitial.setUnitId(resources.getString(R.string.ad_unit_id_for_interstitial))
        FixedInterstitial.load(this)


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        tabs = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        background = findViewById(R.id.background)
        toolbar = findViewById(R.id.toolbar)

        accountViewModel = MyApplication.getAccountViewModel(this)
        characterViewModel = ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)

        character = intent.getParcelableExtra(INTENT_CHARACTER)!!
        val characterLiveData = characterViewModel.getCharacter(character.id)
        characterLiveData.observe(this, Observer { character ->
            if (character == null) return@Observer
            this@CharacterDetailActivity.character = character
            initializeBackground(character)
            initializeTab(character)
            initializeToolbar(character)
        })
    }

    override fun onResume() {
        super.onResume()
        adMobManager.checkAndLoad()
    }

    private fun initializeBackground(character: RecommendCharacter) {
        background.background = character.getBackgroundDrawable(this@CharacterDetailActivity, 1000, 1000)
        background.alpha = character.backgroundImageOpacity
    }

    private fun initializeTab(character: RecommendCharacter) {
        tabs.tabTextColors = ColorStateList.valueOf(character.getHomeTextColor())
    }

    private fun accountToolbarBackgroundColor(account: Account?): Int {
        return account?.getToolbarBackgroundColor() ?: Color.parseColor("#eb34ab")
    }

    private fun accountToolbarTextColor(account: Account?): Int {
        return account?.getToolbarTextColor() ?: Color.parseColor("#ffffff")
    }

    private fun initializeToolbar(character: RecommendCharacter) {
        val account = accountViewModel.accountLiveData.value
        toolbar.setBackgroundColor(character.getToolbarBackgroundColor(this, accountToolbarBackgroundColor(account)))
        toolbar.setTitleTextColor(character.getToolbarTextColor(this, accountToolbarTextColor(account)))
        val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
        if (drawable != null) {
            DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, accountToolbarTextColor(account)))
        }
        MyApplication.setupStatusBarColor(this,
                character.getToolbarTextColor(this, accountToolbarTextColor(account)),
                character.getToolbarBackgroundColor(this, accountToolbarBackgroundColor(account)))
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
                } else {
                    item.setIcon(R.drawable.pin_fill)
                    it.setFixedCharacter(character.id)
                    accountViewModel.saveAccount(it)
                    Toast.makeText(this@CharacterDetailActivity, "トップページ に固定しました", Toast.LENGTH_SHORT).show()
                }
                setMenuItemIconTint(account, item)
            }
            false
        }
        account?.let {
            if (it.getFixedCharacterId() == null) {
                item.setIcon(R.drawable.pin_outline)
            } else {
                item.setIcon(R.drawable.pin_fill)
            }
            setAllMenuItemIconTint(it, menu)
        }
        return true
    }

    private fun setMenuItemIconTint(account: Account, item: MenuItem) {
        var drawable = item.icon
        drawable = drawable?.let { DrawableCompat.wrap(it) }
        if (drawable != null) {
            DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, account.getToolbarTextColor()))
        }
    }

    private fun setAllMenuItemIconTint(account: Account, menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            var drawable = item.icon
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, account.getToolbarTextColor()))
            }
        }
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
            R.id.setting_toolbar -> {
                val toolbarSettingDialogFragment = ToolbarSettingDialogFragment(object : DialogActionListener<ToolbarSettingDialogFragment> {
                    override fun onDecision(dialog: ToolbarSettingDialogFragment) {
                        character.toolbarBackgroundColor = dialog.backgroundColor
                        character.toolbarTextColor = dialog.textColor
                        characterViewModel.update(character)
                    }

                    override fun onCancel() {}
                })
                val account = accountViewModel.accountLiveData.value
                toolbarSettingDialogFragment.setDefaultBackgroundColor(character.getToolbarBackgroundColor(this, accountToolbarBackgroundColor(account)))
                toolbarSettingDialogFragment.setDefaultTextColor(character.getToolbarTextColor(this, accountToolbarTextColor(account)))
                toolbarSettingDialogFragment.show(supportFragmentManager, ToolbarSettingDialogFragment.TAG)
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