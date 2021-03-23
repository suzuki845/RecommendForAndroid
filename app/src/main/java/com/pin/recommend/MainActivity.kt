package com.pin.recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.pin.recommend.Constants.PREF_KEY_IS_LOCKED
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.util.PrefUtil
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = MyApplication.getAccountViewModel(this)

        val characterListIntent = Intent(this, CharacterListActivity::class.java)
        val intents = ArrayList<Intent>()
        intents.add(characterListIntent)
        val fixedCharacter = Transformations.switchMap(accountViewModel.accountLiveData) { input ->
            val characterDao = AppDatabase.getDatabase(this@MainActivity)
                    .recommendCharacterDao()
            var fixedCharacterId: Long? = -1L
            if (input != null) {
                fixedCharacterId = input.fixedCharacterId
            }
            if (fixedCharacterId == null) {
                fixedCharacterId = -1L
            }
            characterDao.findTrackedById(fixedCharacterId)
        }
        /*
        fixedCharacter.observe(this, Observer { character ->
            if (character != null) {
                val characterDetailIntent = Intent(this@MainActivity, CharacterDetailActivity::class.java)
                characterDetailIntent.putExtra(CharacterDetailActivity.INTENT_CHARACTER, character)
                intents.add(characterDetailIntent)
            }
            startActivities(intents.toArray(arrayOf()))
            finish()
        })
         */
        fixedCharacter.observe(this, Observer { character ->
            if (character != null) {
                val characterDetailIntent = Intent(this@MainActivity, CharacterDetailActivity::class.java)
                characterDetailIntent.putExtra(CharacterDetailActivity.INTENT_CHARACTER, character)
                intents.add(characterDetailIntent)
            }
            if (isNeedPassCodeConfirmation && PrefUtil.getBoolean(PREF_KEY_IS_LOCKED)) {
                intents.add(PassCodeConfirmationActivity.createIntent(applicationContext));
            }
            isNeedPassCodeConfirmation = false;

            startActivities(intents.toArray(arrayOf()))
            finish()
        })


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