package com.pin.recommend.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.pin.recommend.R

class SimpleDialogFragmentActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_dialog_fragment)

        val fragment = SimpleDialogFragment(
            title = "", message = "", onPositive = { it.dismiss() },
            onNegative = { it.dismiss() },
        )

        fragment.show(supportFragmentManager, SimpleDialogFragment.TAG)
    }

    companion object {
        val INTENT_AD_UNIT_ID = "SimpleDialogFragmentActivity.INTENT_AD_UNIT_ID"
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, SimpleDialogFragmentActivity::class.java)
            return intent
        }
    }

}

class SimpleDialogFragment(
    val title: String?,
    val message: String?,
    val onPositive: (DialogInterface) -> Unit,
    val onNegative: (DialogInterface) -> Unit,
) : DialogFragment() {

    companion object {
        const val TAG = "com.pin.recommend.DialogFragment.TAG";
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_reward, null)

        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.message).text = message
        val builder = AlertDialog.Builder(activity)
        val d = builder
            .setView(view)
            .setPositiveButton("はい") { dialog: DialogInterface, i: Int ->
                onPositive(dialog)
            }
            .setNegativeButton("いいえ") { dialog: DialogInterface, i: Int ->
                onNegative(dialog)
            }
            .create()
        d.setCanceledOnTouchOutside(false);
        return d
    }

}