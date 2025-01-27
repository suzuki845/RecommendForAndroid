package com.pin.recommend.ui.globalsetting

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import com.pin.recommend.ui.passcode.PassCodeSetActivity


class GlobalSettingActivity : AppCompatActivity() {

    private val vm by lazy { ViewModelProvider(this)[GlobalSettingViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Body(vm, vm.state.collectAsState(GlobalSettingViewModelState()).value)
        }
    }

    @Composable
    fun Body(
        vm: GlobalSettingViewModel,
        state: GlobalSettingViewModelState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text("設定")
                    },
                    actions = {
                    },
                )
            },
            bottomBar = {
                AdaptiveBanner(adId = resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            ActionStatus(state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Content(vm, state)
            }
        }
    }

    @Composable
    fun ActionStatus(state: GlobalSettingViewModelState) {
        if (state.action == GlobalSettingViewModelAction.BackupExport && state.status == GlobalSettingViewModelStatus.Success) {
            Toast.makeText(
                this@GlobalSettingActivity,
                "バックアップを作成しました。",
                Toast.LENGTH_LONG
            ).show()
        }
        if (state.action == GlobalSettingViewModelAction.BackupImport && state.status == GlobalSettingViewModelStatus.Success) {
            Toast.makeText(
                this@GlobalSettingActivity,
                "バックアップを復元しました。",
                Toast.LENGTH_LONG
            ).show()
        }
        if (state.action == GlobalSettingViewModelAction.UnLockPassCode && state.status == GlobalSettingViewModelStatus.Success) {
            Toast.makeText(this, "パスコードを解除しました。", Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    fun ErrorMessage(
        vm: GlobalSettingViewModel,
        state: GlobalSettingViewModelState
    ) {
        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { vm.resetError() },
                title = { Text("Error") },
                text = { Text(state.errorMessage) },
                confirmButton = {
                    TextButton(onClick = { vm.resetError() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    @Composable
    fun Content(
        vm: GlobalSettingViewModel,
        state: GlobalSettingViewModelState
    ) {
        val self = this
        Column {
            Row(
                Modifier
                    .padding(6.dp)
                    .height(30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("パスコード")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = state.isPassCodeLock,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            startActivity(PassCodeSetActivity.createIntent(self))
                        } else {
                            vm.unlockPassCode()
                        }
                    }
                )
            }
            Divider()
            Box(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .clickable {
                        onExportBackup()
                    }) {
                Text("バックアップの作成")
            }
            Divider()
            Box(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .clickable {
                        onImportBackup()
                    }) {
                Text("バックアップの復元")
            }
            Divider()
            Box(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.privacyPolicyUri);
                        startActivity(intent)
                    }) {
                Text("プライバシーポリシー")
            }
            Divider()
            Box(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.homePageUri);
                        startActivity(intent)
                    }) {
                Text("お問い合わせ")
            }
            Section {
                Text("きっとあなたが気に入るアプリ")
            }
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.oshiTimerUri);
                        startActivity(intent)
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = painterResource(R.drawable.oshitimer),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text("推しと集中タイマー")
            }
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.oshiDietUri);
                        startActivity(intent)
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp)),

                    painter = painterResource(R.drawable.diet_support),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text("推しダイエット")
            }
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.emotionDiaryUri);
                        startActivity(intent)
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp)),

                    painter = painterResource(R.drawable.emotion_diary),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text("気分を記録できるつぶやき日記　Meemo(ミーモ)")
            }
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        intent = Intent(Intent.ACTION_VIEW, state.wordBookUri);
                        startActivity(intent)
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp)),

                    painter = painterResource(R.drawable.wordbook),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text("推しと学ぶ韓国語・英語")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        vm.checkPassCodeLock()
    }

    private fun onExportBackup() {
        Toast.makeText(this, "バックアップの作成先を選択して下さい。", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, EXPORT_BACKUP_REQUEST_CODE)
    }

    private fun onImportBackup() {
        val dialog =
            BackupImportDialogFragment(object :
                DialogActionListener<BackupImportDialogFragment> {
                override fun onDecision(dialog: BackupImportDialogFragment) {
                    Toast.makeText(
                        this@GlobalSettingActivity,
                        "バックアップフォルダを選択して下さい。",
                        Toast.LENGTH_LONG
                    ).show()
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
                vm.backupExport(result?.data)
            }

            if (requestCode == IMPORT_BACKUP_REQUEST_CODE) {
                vm.backupImport(result?.data)
            }

        }
    }

}


class BackupImportDialogFragment(private val actionListener: DialogActionListener<BackupImportDialogFragment>) :
    DialogFragment() {
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
        dismiss()
    }

    companion object {
        const val Tag = "com.pin.reccomend.DeleteDialogFragment"
    }
}
