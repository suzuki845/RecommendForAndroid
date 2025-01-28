package com.pin.recommend.ui.anniversary

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.domain.model.CharacterDetailsState
import com.pin.recommend.util.admob.ContentResolverUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class AnniversaryScreenShotViewModelAction {
    Init,
    Save
}

enum class AnniversaryScreenShotViewModelStatus {
    Processing,
    Success,
    Failure
}

data class AnniversaryScreenShotViewModelState(
    val action: AnniversaryScreenShotViewModelAction = AnniversaryScreenShotViewModelAction.Init,
    val status: AnniversaryScreenShotViewModelStatus = AnniversaryScreenShotViewModelStatus.Processing,
    val characterDetailsState: CharacterDetailsState = CharacterDetailsState(),
    val picture: Picture? = null,
    val errorMessage: String? = null,
)

class AnniversaryScreenShotViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AnniversaryScreenShotViewModelState())

    val state: Flow<AnniversaryScreenShotViewModelState> = _state

    fun setCharacterDetailsState(state: CharacterDetailsState) {
        _state.value = _state.value.copy(
            characterDetailsState = state
        )
    }

    fun setScreenshot(picture: Picture) {
        _state.value = _state.value.copy(
            picture = picture
        )
    }

    private fun insertImage(
        bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        return ContentResolverUtil.insertImage(
            getApplication(),
            bitmap,
            format,
            mimeType,
            displayName
        )
    }

    fun saveScreenshot() {
        try {
            _state.value = _state.value.copy(
                AnniversaryScreenShotViewModelAction.Save,
                AnniversaryScreenShotViewModelStatus.Processing
            )
            val picture = _state.value.picture ?: throw Exception("picture is null")

            val bitmap = Bitmap.createBitmap(
                picture.width,
                picture.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawPicture(picture)

            insertImage(
                bitmap,
                Bitmap.CompressFormat.PNG,
                "image/png",
                "anniversary-${System.currentTimeMillis()}"
            )

            _state.value = _state.value.copy(
                AnniversaryScreenShotViewModelAction.Save,
                AnniversaryScreenShotViewModelStatus.Success
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                AnniversaryScreenShotViewModelAction.Save,
                AnniversaryScreenShotViewModelStatus.Processing,
                errorMessage = e.message
            )
        }
    }

    fun resetError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }

}