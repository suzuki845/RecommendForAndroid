package com.pin.recommend.ui.payment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.model.WholePeriodCharacterPaymentModel
import com.pin.recommend.util.admob.ContentResolverUtil
import com.pin.recommend.util.combine3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class PaymentWholePeriodViewModelAction {
    Init,
    Save
}

enum class PaymentWholePeriodViewModelStatus {
    Processing,
    Success,
    Failure
}

data class PaymentWholePeriodViewModelState(
    val status: PaymentWholePeriodViewModelStatus = PaymentWholePeriodViewModelStatus.Processing,
    val action: PaymentWholePeriodViewModelAction = PaymentWholePeriodViewModelAction.Init,
    val characterName: String = "",
    val appearance: Appearance = Appearance(),
    val paymentAmount: Int = 0,
    val savingsAmount: Int = 0,
    val picture: Picture? = null,
    val errorMessage: String? = null
)

class PaymentWholePeriodViewModel(private val application: Application) :
    AndroidViewModel(application) {

    private val model by lazy {
        WholePeriodCharacterPaymentModel(application)
    }

    private val combineData = combine3(
        model.character,
        model.wholePeriodPaymentAmount,
        model.wholePeriodSavingsAmount
    ) { a, b, c ->
        PaymentWholePeriodViewModelState(
            characterName = a?.name ?: "",
            appearance = a?.appearance(application) ?: Appearance(),
            paymentAmount = b ?: 0,
            savingsAmount = c ?: 0
        )
    }

    private val _state = MutableStateFlow(PaymentWholePeriodViewModelState())

    val state: Flow<PaymentWholePeriodViewModelState> = _state

    fun observe(owner: LifecycleOwner) {
        combineData.observe(owner) {
            _state.value = it
        }
    }

    fun setCharacterId(id: Long) {
        model.setCharacterId(id)
    }

    fun setCharacterId(id: Long?) = model.setCharacterId(id)

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
                action = PaymentWholePeriodViewModelAction.Save,
                status = PaymentWholePeriodViewModelStatus.Processing
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
                "payment-${System.currentTimeMillis()}"
            )

            _state.value = _state.value.copy(
                action = PaymentWholePeriodViewModelAction.Save,
                status = PaymentWholePeriodViewModelStatus.Success
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = PaymentWholePeriodViewModelAction.Save,
                status = PaymentWholePeriodViewModelStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = "")
    }
}