package com.pin.recommend

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pin.recommend.CharacterDetailActivity
import com.pin.recommend.CreateCharacterActivity
import com.pin.recommend.adapter.CharactersAdapter
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import com.pin.recommend.model.viewmodel.EditStateViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.util.AdMobAdaptiveBannerManager
import com.pin.util.Reward.Companion.getInstance

class CharacterListActivity : AppCompatActivity() {
    private val charactersAdapter: CharactersAdapter by lazy {
        CharactersAdapter(this, characterViewModel)
    }
    private val characterViewModel: RecommendCharacterViewModel by lazy {
        ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)
    }
    private val accountViewModel: AccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }
    private val editListViewModel: EditStateViewModel by lazy {
        ViewModelProvider(this).get(EditStateViewModel::class.java)
    }
    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private var adViewContainer: ViewGroup? = null

    private lateinit var charactersListView: ListView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_list)
        adViewContainer = findViewById(R.id.ad_container)
        adMobManager =
            AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id))
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = getInstance(this)
        reward.isBetweenRewardTime.observe(this
        ) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime)
            adMobManager.checkFirst()
        }

        charactersListView = findViewById(R.id.characters_listview)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        charactersListView.setAdapter(charactersAdapter)
        charactersListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, position, id ->
                val intent = Intent(this@CharacterListActivity, CharacterDetailActivity::class.java)
                intent.putExtra(
                    CharacterDetailActivity.INTENT_CHARACTER,
                    id
                )
                startActivity(intent)
            }

        toolbar = findViewById(R.id.toolbar)
        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this
        ) { account -> initializeToolbar(account) }
        val characters = characterViewModel.getCharacters(accountLiveData)
        characters.observe(this
        ) { recommendCharacters -> charactersAdapter!!.setList(recommendCharacters) }
        editListViewModel.editMode.observe(this
        ) { aBoolean -> charactersAdapter.setEditMode(aBoolean) }
        fab.setOnClickListener {
            val intent = Intent(this@CharacterListActivity, CreateCharacterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    private fun initializeToolbar(account: Account) {
        toolbar!!.title = "推しリスト"
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        editListViewModel!!.editMode.observe(this
        ) { mode ->
            if (mode) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_mode -> {
                if (editListViewModel!!.editMode.value!!) {
                    editListViewModel!!.setEditMode(false)
                } else {
                    editListViewModel!!.setEditMode(true)
                }
                true
            }
            R.id.setting -> {
                startActivity(Intent(this, GlobalSettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}