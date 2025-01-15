package com.pin.recommend.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.Constants.PREF_KEY_IS_LOCKED
import com.pin.recommend.ui.character.CharacterDetailActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterListActivity
import com.pin.recommend.ui.passcode.PassCodeConfirmationActivity
import com.pin.recommend.util.PrefUtil
import com.pin.util.admob.reward.RemoveAdReward

class MainActivity : AppCompatActivity() {

    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this).get(CharacterDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterListIntent = Intent(this, CharacterListActivity::class.java)
        val intents = ArrayList<Intent>()
        intents.add(characterListIntent)

        vm.observePinningCharacterId(this) { pinningId ->
            if (pinningId != null) {
                val characterDetailIntent =
                    Intent(this@MainActivity, CharacterDetailActivity::class.java)
                characterDetailIntent.putExtra(
                    CharacterDetailActivity.INTENT_CHARACTER,
                    pinningId
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

        val reward = RemoveAdReward.getInstance(this)
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
        const val INTENT_ACCOUNT = "com.pin.recommend.view.MainActivity.INTENT_ACCOUNT"
    }
}

