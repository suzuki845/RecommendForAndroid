package com.pin.recommend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.dialog.ToolbarSettingDialogFragment
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.BackupExportModel
import com.pin.recommend.model.BackupImportModel
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.util.PrefUtil
import com.pin.recommend.util.ShowToast
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
                ShowToast.show("ロックを解除しました。", this);
            }
        })
    }

    fun onExportBackup(v: View){

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, EXPORT_BACKUP_REQUEST_CODE)
    }

    fun onImportBackup(v: View){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, IMPORT_BACKUP_REQUEST_CODE)
    }


    private val EXPORT_BACKUP_REQUEST_CODE = 21389
    private val IMPORT_BACKUP_REQUEST_CODE = 21726
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode == RESULT_OK) {
            if (requestCode == EXPORT_BACKUP_REQUEST_CODE) {
                if (result?.data != null) {
                    val pickedDir = DocumentFile.fromTreeUri(this, result.data!!)
                    GlobalScope.launch {
                        backupExporter.export(this@GlobalSettingActivity, pickedDir!!)
                    }
                }
            }

            if (requestCode == IMPORT_BACKUP_REQUEST_CODE) {
                if (result?.data != null) {
                    val pickedDir = DocumentFile.fromTreeUri(this, result.data!!)
                    GlobalScope.launch {
                        backupImporter.import(this@GlobalSettingActivity, pickedDir!!)
                    }
                }
            }

        }
    }

    fun onClickSettingToolbar(v: View){
        val account = accountViewModel.accountLiveData.value
        val toolbarSettingDialogFragment = ToolbarSettingDialogFragment(object : DialogActionListener<ToolbarSettingDialogFragment> {
            override fun onDecision(dialog: ToolbarSettingDialogFragment?) {
                account?.toolbarBackgroundColor = dialog?.backgroundColor;
                account?.toolbarTextColor = dialog?.textColor;
                accountViewModel.saveAccount(account);
            }

            override fun onCancel() {
            }
        });
        if (account != null) {
            toolbarSettingDialogFragment.setDefaultBackgroundColor(account.toolbarBackgroundColor)
        }
        if (account != null) {
            toolbarSettingDialogFragment.setDefaultTextColor(account.getToolbarTextColor())
        }
        toolbarSettingDialogFragment.show(supportFragmentManager, ToolbarSettingDialogFragment.TAG);
    }

    fun onClickPrivacyPolicy(v: View){
        val uri = Uri.parse("http://turuwo-apps.net/privacy-policy.html");
        intent = Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private fun initializeToolbar(account: Account?) {
        if (account != null) {
            toolbar.setBackgroundColor(account.getToolbarBackgroundColor())
            toolbar.setTitleTextColor(account.getToolbarTextColor())
            val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
            drawable?.let { DrawableCompat.setTint(it, account.getToolbarTextColor()) }
            MyApplication.setupStatusBarColor(this, account.getToolbarTextColor(), account.getToolbarBackgroundColor())
            toolbar.title = "設定"
            setSupportActionBar(toolbar)
        }
    }

}