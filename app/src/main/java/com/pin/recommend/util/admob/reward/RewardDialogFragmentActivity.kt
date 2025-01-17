package com.pin.util.admob.reward

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.pin.util.R
import com.pin.util.admob.RewardAdStateAction

class RewardDialogFragmentActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_dialog_fragment)

        val adUnitId = intent.getStringExtra(INTENT_AD_UNIT_ID)!!

        val fragment = RewardDialogFragment(adUnitId, onOk = { it.dismiss() },
            onCancel = { it.dismiss() },
            onStop = { it.requireActivity().finish() })

        fragment.show(supportFragmentManager, RewardDialogFragment.TAG)
    }

    companion object {
        val INTENT_AD_UNIT_ID = "RewardDialogFragmentActivity.INTENT_AD_UNIT_ID"
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, RewardDialogFragmentActivity::class.java)
            return intent
        }
    }

}

class RewardDialogFragment(
    val adUnitId: String,
    val onOk: (DialogInterface) -> Unit,
    val onCancel: (DialogInterface) -> Unit,
    val onStop: (RewardDialogFragment) -> Unit
) : DialogFragment() {

    companion object {
        const val TAG = "com.pin.recommend.RewardDialogFragment.TAG";
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_reward, null)

        view.findViewById<TextView>(R.id.title).setText("動画広告を見ると広告を削除できます")
        view.findViewById<TextView>(R.id.message).setText("視聴すると16時間広告が表示されなくなります。")
        val builder = AlertDialog.Builder(activity)

        val d = builder
            .setView(view)
            .setPositiveButton("視聴する") { dialog: DialogInterface, i: Int ->
                val progress = ProgressDialog(activity)
                progress.show()
                onOk(dialog)
                val reward = Reward(adUnitId)
                reward.show(requireActivity(), RewardAdStateAction(
                    onLoading = {},
                    onLoadComplete = {
                        progress.dismiss()
                    },
                    onLoadFailed = { _ ->
                        progress.dismiss()
                    },
                    onShowComplete = {},
                    onShowFailed = { _ -> },
                    onUserEarnedReward = { _ -> }
                ))
            }
            .setNegativeButton("キャンセル") { dialog: DialogInterface, i: Int ->
                val removeAdReward = RemoveAdReward.getInstance(requireActivity())
                removeAdReward.noThanks()
                onCancel(dialog)
            }
            .create()
        d.setCanceledOnTouchOutside(false);
        return d
    }

    override fun onStop() {
        super.onStop()
        onStop(this)
    }
}