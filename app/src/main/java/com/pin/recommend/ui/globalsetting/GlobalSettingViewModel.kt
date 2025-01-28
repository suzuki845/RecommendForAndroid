package com.pin.recommend.ui.globalsetting

import android.app.Application
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pin.recommend.Constants
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.model.BackupExporter
import com.pin.recommend.domain.model.BackupImporter
import com.pin.recommend.util.PrefUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class GlobalSettingViewModelAction {
    Init,
    BackupImport,
    BackupExport,
    UnLockPassCode
}

enum class GlobalSettingViewModelStatus {
    Processing,
    Success,
    Failure,
}

data class GlobalSettingViewModelState(
    val action: GlobalSettingViewModelAction = GlobalSettingViewModelAction.Init,
    val status: GlobalSettingViewModelStatus = GlobalSettingViewModelStatus.Success,
    val isPassCodeLock: Boolean = false,
    val errorMessage: String? = null,
) {
    val homePageUri get() = Uri.parse("https://developer-d5452.web.app/")
    val privacyPolicyUri get() = Uri.parse("https://developer-d5452.web.app/privacy-policy.html")
    val oshiTimerUri get() = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.oshitimer")
    val emotionDiaryUri get() = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.emotiondiary")
    val oshiDietUri get() = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.diet_support")
    val wordBookUri get() = Uri.parse("https://play.google.com/store/apps/details?id=com.suzuki.wordbook")

}

class GlobalSettingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val backupExporter = BackupExporter(db)
    private val backupImporter = BackupImporter(db)
    private val pref by lazy { PrefUtil(getApplication()) }

    private val _state = MutableStateFlow(
        GlobalSettingViewModelState(
            isPassCodeLock = pref.getBoolean(Constants.PREF_KEY_IS_LOCKED)
        )
    )
    val state: Flow<GlobalSettingViewModelState> = _state

    fun unlockPassCode() {
        _state.value = _state.value.copy(
            action = GlobalSettingViewModelAction.UnLockPassCode,
            status = GlobalSettingViewModelStatus.Processing,
        )
        pref.putBoolean(Constants.PREF_KEY_IS_LOCKED, false);
        pref.putInt(Constants.PREF_KEY_PASSWORD, 0);
        _state.value = _state.value.copy(
            isPassCodeLock = false
        )
        _state.value = _state.value.copy(
            action = GlobalSettingViewModelAction.UnLockPassCode,
            status = GlobalSettingViewModelStatus.Success,
        )
    }

    fun checkPassCodeLock() {
        _state.value = _state.value.copy(
            isPassCodeLock = pref.getBoolean(Constants.PREF_KEY_IS_LOCKED)
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }

    fun backupExport(exportDirUri: Uri?) {
        viewModelScope.launch {
            try {
                if (exportDirUri == null) throw Exception("エクスポート先が選択されていません。")
                val pickedDir =
                    DocumentFile.fromTreeUri(getApplication(), exportDirUri)
                        ?: throw Exception("エクスポート先が見つかりませんでした。")

                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupExport,
                    status = GlobalSettingViewModelStatus.Processing,
                )

                backupExporter.export(getApplication(), pickedDir)

                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupExport,
                    status = GlobalSettingViewModelStatus.Success,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupExport,
                    status = GlobalSettingViewModelStatus.Failure,
                    errorMessage = e.message
                )
            }
        }
    }

    fun backupImport(importFileUri: Uri?) {
        viewModelScope.launch {
            try {
                if (importFileUri == null) throw Exception("インポートするファイルが選択されていません。")
                val file =
                    DocumentFile.fromTreeUri(getApplication(), importFileUri)
                        ?: throw Exception("ファイルが見つかりませんでした。。")

                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupImport,
                    status = GlobalSettingViewModelStatus.Processing,
                )

                backupImporter.import(getApplication(), file)

                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupImport,
                    status = GlobalSettingViewModelStatus.Success,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    action = GlobalSettingViewModelAction.BackupImport,
                    status = GlobalSettingViewModelStatus.Failure,
                    errorMessage = e.message
                )
            }
        }
    }

}