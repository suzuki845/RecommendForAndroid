package com.pin.recommend

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.pin.recommend.util.PrefUtil


class NotificationChecker(val context: Context) {

    companion object {
        const val NOTIFICATION_COUNT = "NotificationChecker.CHECK"
        const val NOTIFICATION_DATA = "NotificationChecker.DATA"
    }

    fun check(check: Int, intents: ArrayList<Intent>, data: NotificationData){
        val appStartCount = PrefUtil.getInt(Constants.APP_START_COUNT)
        val notificationCount = PrefUtil.getInt(NOTIFICATION_COUNT)
        if(check > notificationCount && appStartCount > 3){
            val isPassCodeLocked = PrefUtil.getBoolean(Constants.PREF_KEY_IS_LOCKED)
            if(!isPassCodeLocked){
                val intent = NotificationDialogActivity.createIntent(context)
                intent.putExtra(NOTIFICATION_DATA, data.toJson())
                intents.add(intent)
                val next = notificationCount + 1;
                PrefUtil.putInt(NOTIFICATION_COUNT, next);
            }
        }
    }

}

data class NotificationData(val icon: Int, val title: String, val message: String, val uri: String){

    fun  toJson(): String{
        return Gson().toJson(this);
    }

    companion object {
        fun fromJson(json: String): NotificationData {
            return Gson().fromJson(json, NotificationData::class.java);
        }
    }

}

class NotificationDialogActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra(NotificationChecker.NOTIFICATION_DATA);
        val data = NotificationData.fromJson(json!!)
        val fragment = NotificationDialogFragment(data)
        fragment.show(supportFragmentManager, NotificationDialogFragment.TAG)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, NotificationDialogActivity::class.java)

            return intent
        }
    }

}


class NotificationDialogFragment(val data: NotificationData) : DialogFragment() {

    companion object{
        const val TAG = "com.pin.recommend.NotificationDialogFragment.TAG";
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(com.pin.recommend.R.layout.dialog_new_app_release ,null)

        view.findViewById<TextView>(com.pin.recommend.R.id.title).setText(data.title)
        view.findViewById<ImageView>(com.pin.recommend.R.id.icon).setImageResource(data.icon)
        view.findViewById<TextView>(com.pin.recommend.R.id.message).setText(data.message)
        val builder =  AlertDialog.Builder(activity)
        return builder
            .setView(view)
            .setPositiveButton("インストール") { _: DialogInterface, i: Int ->
                val uri = Uri.parse(data.uri);
                val intent = Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            .setNegativeButton("キャンセル") { dialog: DialogInterface, i: Int ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().finish()
    }
}