package com.pin.recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.pin.recommend.Constants.PREF_KEY_IS_LOCKED
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import com.pin.recommend.util.PrefUtil
import com.pin.util.Interstitial
import com.pin.util.Reward
import java.util.*

class MainActivity : AppCompatActivity() {

    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this).get(CharacterDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterListIntent = Intent(this, CharacterListActivity::class.java)
        val intents = ArrayList<Intent>()
        intents.add(characterListIntent)

        val fixedCharacter = detailsVM.account.switchMap{ input ->
            val characterDao = AppDatabase.getDatabase(this@MainActivity)
                .recommendCharacterDao()
            var fixedCharacterId: Long? = -1L
            if (input != null) {
                fixedCharacterId = input.fixedCharacterId
            }
            if (fixedCharacterId == null) {
                fixedCharacterId = -1L
            }
            characterDao.watchById(fixedCharacterId)
        }

        fixedCharacter.observe(this) { character ->
            if (character != null) {
                val characterDetailIntent =
                    Intent(this@MainActivity, CharacterDetailActivity::class.java)
                characterDetailIntent.putExtra(
                    CharacterDetailActivity.INTENT_CHARACTER,
                    character.id
                )
                intents.add(characterDetailIntent)
            }
            if (isNeedPassCodeConfirmation && PrefUtil.getBoolean(PREF_KEY_IS_LOCKED)) {
                intents.add(PassCodeConfirmationActivity.createIntent(applicationContext))
            }
            isNeedPassCodeConfirmation = false
            startActivities(intents.toArray(arrayOf()))
            finish()
        }

        Interstitial.setUnitId(resources.getString(R.string.ad_unit_id_for_interstitial))
        val reward = Reward.getInstance(this)
        reward.setAdUnitId(resources.getString(R.string.ad_unit_id_for_reward))
        //reward.reset()
        reward.checkRewardTime()
        reward.checkNotify()

    }

    private var isNeedPassCodeConfirmation = true
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isNeedPassCodeConfirmation = true
        }
    }

    companion object {
        const val INTENT_ACCOUNT = "com.pin.recommend.MainActivity.INTENT_ACCOUNT"
    }
}

