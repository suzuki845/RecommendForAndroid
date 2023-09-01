package com.pin.recommend

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.dialog.ToolbarSettingDialogFragment
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.BackupExportModel
import com.pin.recommend.model.BackupImportModel
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.util.PrefUtil
import com.pin.util.Reward
import com.pin.util.reward.RewardDialogFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class GlobalSettingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var accountViewModel: AccountViewModel

    private lateinit var passCodeRock: Switch

    private val backupExporter by lazy {
        BackupExportModel(
                AppDatabase.getDatabase(this)
        )
    }

    private val backupImporter by lazy {
        BackupImportModel(
                AppDatabase.getDatabase(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_setting)
        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar = findViewById(R.id.toolbar)

        val showReward = findViewById<ViewGroup>(R.id.show_reward)
        val reward = Reward.getInstance(this)
        reward.isBetweenRewardTime.observe(this) {
            if(!it){
                showReward.visibility = View.VISIBLE
            }else{
                showReward.visibility = View.GONE
            }
        }

        accountViewModel = MyApplication.getAccountViewModel(this)
        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this, Observer { account -> initializeToolbar(account) })

        passCodeRock = findViewById(R.id.passcode_rock)
    }

    override fun onResume() {
        super.onResume()
        passCodeRock.isChecked = PrefUtil.getBoolean(Constants.PREF_KEY_IS_LOCKED)
        passCodeRock.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startActivity(PassCodeSetActivity.createIntent(this))
            } else {
                PrefUtil.putBoolean(Constants.PREF_KEY_IS_LOCKED, false);
                PrefUtil.putInt(Constants.PREF_KEY_PASSWORD, 0);
                Toast.makeText(this, "ロックを解除しました。", Toast.LENGTH_SHORT).show();
            }
        })
    }

    fun onExportBackup(v: View){
        Toast.makeText(this, "バックアップの作成先を選択して下さい。", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, EXPORT_BACKUP_REQUEST_CODE)
    }

    fun onImportBackup(v: View){
        val dialog = BackupImportDialogFragment(object : DialogActionListener<BackupImportDialogFragment> {
            override fun onDecision(dialog: BackupImportDialogFragment) {
                Toast.makeText(this@GlobalSettingActivity, "バックアップフォルダを選択して下さい。", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivityForResult(intent, IMPORT_BACKUP_REQUEST_CODE)
            }
            override fun onCancel() {}
        })
        dialog.show(supportFragmentManager, "GlobalSettingActivity")
    }


    private val EXPORT_BACKUP_REQUEST_CODE = 21389
    private val IMPORT_BACKUP_REQUEST_CODE = 21726
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode == RESULT_OK) {
            if (requestCode == EXPORT_BACKUP_REQUEST_CODE) {
                result?.data?.let {data ->
                    val pickedDir = DocumentFile.fromTreeUri(this, data) ?:
                    return Toast.makeText(this@GlobalSettingActivity, "バックアップの作成に失敗しました。", Toast.LENGTH_LONG).show()
                    GlobalScope.launch {
                        try{
                            backupExporter.export(this@GlobalSettingActivity, pickedDir)
                            this@GlobalSettingActivity.runOnUiThread{
                                Toast.makeText(this@GlobalSettingActivity, "バックアップを作成しました。", Toast.LENGTH_LONG).show()
                            }
                        }catch (e: Exception){
                            this@GlobalSettingActivity.runOnUiThread{
                                Toast.makeText(this@GlobalSettingActivity, "バックアップの作成に失敗しました。\n\n${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

            if (requestCode == IMPORT_BACKUP_REQUEST_CODE) {
                result?.data?.let {data ->
                    val pickedDir = DocumentFile.fromTreeUri(this, data)
                            ?: return  Toast.makeText(this@GlobalSettingActivity, "バックアップの復元に失敗しました。", Toast.LENGTH_LONG).show()
                    GlobalScope.launch {
                        try {
                            backupImporter.import(this@GlobalSettingActivity, pickedDir)
                            this@GlobalSettingActivity.runOnUiThread{
                                Toast.makeText(this@GlobalSettingActivity, "バックアップを復元しました。", Toast.LENGTH_LONG).show()
                            }
                        }catch (e: Exception){
                            this@GlobalSettingActivity.runOnUiThread{
                                Toast.makeText(this@GlobalSettingActivity, "バックアップの復元に失敗しました。\n\n${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

        }
    }

    fun onClickHomePage(v: View){
        val uri = Uri.parse("https://developer-d5452.web.app/");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    fun onClickPrivacyPolicy(v: View){
        val uri = Uri.parse("https://developer-d5452.web.app/privacy-policy.html");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private fun initializeToolbar(account: Account?) {
        toolbar.title = "設定"
        setSupportActionBar(toolbar)
    }

    fun onShowRewardDialog(v: View){
        RewardDialogFragment(onOk = {it.dismiss()}, onCancel = {it.dismiss()}, onStop = {}).show(supportFragmentManager, RewardDialogFragment.TAG)
    }

    fun onClickEmotionDiary(v: View){
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.emotiondiary");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    fun onClickDietSupport(v: View){
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.diet_support");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    fun onClickWordBook(v: View){
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.wordbook");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


}


class BackupImportDialogFragment(private val actionListener: DialogActionListener<BackupImportDialogFragment>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setMessage("oshi_backup(日付)のフォルダを選択し、アクセスの許可をタップすると復元が行われます。\n\n警告：バックアップを復元すると現在のデータが全て消去され、バックアップの内容で上書きされます。")
                .setTitle("バックアップの復元")
                .setPositiveButton("決定") { dialog, id -> actionListener.onDecision(this) }
                .setNegativeButton("閉じる") { dialog, id -> actionListener.onCancel() }
                .create()
    }

    override fun onPause() {
        super.onPause()
        // onPause でダイアログを閉じる場合
        dismiss()
    }

    companion object {
        const val Tag = "com.pin.reccomend.DeleteDialogFragment"
    }
}
