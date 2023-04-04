package com.pin.recommend

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.pin.imageutil.CaptureConfig
import com.pin.recommend.Constants.PREF_KEY_IS_LOCKED
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.util.PrefUtil
import com.pin.util.Interstitial
import com.pin.util.Reward
import com.pin.util.reward.RewardDialogFragmentActivity
import kotlinx.coroutines.delay
import java.net.URI
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

            val notification = NotificationChecker(applicationContext);
            /*
            val data = NotificationData(
                R.drawable.emotion_diary,
                "新アプリリリースのお知らせ",
                "独り言をつぶやくように、簡単にその時の感情を記録できる、誰にも見られないチャット風の気分日記アプリです。" +
                        "\n\n推して何日？のユーザーさんならこんなアプリがあったら楽しんでくれるんじゃないかなぁと思って作ってみました。" +
                        "\n\n気分アイコン、背景を自由に設定できるので推し仕様にカスタマイズできたりします！" +
                        "\n\n唐突な宣伝で申し訳ないのですが、是非インストールしてみてください！",
                "https://play.google.com/store/apps/details?id=com.suzuki.emotiondiary")
             */
            val data = NotificationData(
                R.drawable.oshitimer,
                "新アプリリリースのお知らせ",
                "推しが応援してくれるタイマーアプリを作ってみました！勉強やトレーニングでご利用ください！" +
                        "\n\n推して何日？のユーザーさんならこんなアプリがあったら楽しんでくれるんじゃないかなぁと思って作ってみました。" +
                        "\n\nアイコン、背景を自由に設定できるので推し仕様にカスタマイズできます！" +
                        "\n\n唐突な宣伝で申し訳ないのですが、是非インストールしてみてください！",
                "https://play.google.com/store/apps/details?id=com.suzuki.oshitimer")
            notification.check(1, intents, data)

            startActivities(intents.toArray(arrayOf()))
            finish()
        })

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

