package com.pin.recommend

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.adapter.CharactersAdapter
import com.pin.recommend.model.viewmodel.CharacterListViewModel
import com.pin.recommend.model.viewmodel.EditStateViewModel
import com.pin.util.admob.AdMobAdaptiveBannerManager
import com.pin.util.admob.reward.RemoveAdReward

class CharacterListActivity : AppCompatActivity() {
    private val charactersAdapter: CharactersAdapter by lazy {
        CharactersAdapter(this, characterListViewModel)
    }
    private val characterListViewModel: CharacterListViewModel by lazy {
        ViewModelProvider(this).get(CharacterListViewModel::class.java)
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
            AdMobAdaptiveBannerManager(
                this,
                adViewContainer,
                getString(R.string.banner_id)
            )
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = RemoveAdReward.getInstance(this)
        reward.isBetweenRewardTime.observe(
            this
        ) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime)
            adMobManager.checkFirst()
        }

        charactersListView = findViewById(R.id.characters_listview)
        charactersListView.adapter = charactersAdapter
        characterListViewModel.characters.observe(this) {
            charactersAdapter.setList(it)
        }

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

        toolbar.title = "推しリスト"
        setSupportActionBar(toolbar)

        editListViewModel.editMode.observe(
            this
        ) { aBoolean -> charactersAdapter.setEditMode(aBoolean) }

    }

    override fun onResume() {
        super.onResume()
        adMobManager!!.checkAndLoad()
    }

    fun destinationSetting(v: View) {
        startActivity(Intent(this, GlobalSettingActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_character_list, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        editListViewModel!!.editMode.observe(
            this
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

            R.id.create -> {
                val intent = Intent(this@CharacterListActivity, CreateCharacterActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}