package com.pin.recommend.ui.gacha

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.R
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.model.gacha.BadgeGachaMachine
import com.pin.recommend.domain.model.gacha.BadgeGachaMachineState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

data class GachaBadgeViewModelState(
    val state: BadgeGachaMachineState = BadgeGachaMachineState(),
    val appearance: Appearance = Appearance(),
) {
    val title = state.title

    val summary = state.summary

    val resultImage = state.resulImage

    val resultMessage = state.resultMessage

    val isRolling
        get(): Boolean {
            return state.isRolling
        }

    val isComplete
        get(): Boolean {
            return state.isComplete
        }
}

class GachaBadgeViewModel(application: Application) : AndroidViewModel(application) {
    private val model = BadgeGachaMachine(AppDatabase.getDatabase(application)).apply {
        setOnPrizeListener {
            val updateWidgetRequest = Intent("android.appwidget.action.APPWIDGET_UPDATE")
            application.sendBroadcast(updateWidgetRequest)
        }
        setNotPrizeImage(
            BitmapFactory.decodeResource(
                application.resources,
                R.drawable.ic_person_300dp
            )
        )
    }

    private val _state = MutableStateFlow(GachaBadgeViewModelState())

    val state: Flow<GachaBadgeViewModelState> = combine(_state, model.state) { a, b ->
        a.copy(
            state = b
        )
    }

    fun observe(owner: LifecycleOwner) {
        model.observe(owner)
    }

    fun reset() {
        model.reset()
    }

    fun setPrizeImage(image: Bitmap?) {
        model.setPrizeImage(image)
    }

    fun setCharacterId(id: Long) {
        model.setCharacterId(id)
    }

    fun setAppearance(appearance: Appearance) {
        _state.value = _state.value.copy(
            appearance = appearance
        )
    }

    fun rollGacha() {
        model.rollGacha()
    }
}