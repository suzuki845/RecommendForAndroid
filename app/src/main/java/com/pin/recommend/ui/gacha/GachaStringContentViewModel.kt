package com.pin.recommend.ui.gacha

import android.app.Application
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.model.gacha.GachaItemAsset
import com.pin.recommend.domain.model.gacha.GachaMachine
import com.pin.recommend.domain.model.gacha.GachaMachineState
import com.pin.recommend.domain.model.gacha.PlaceholderParser
import kotlinx.coroutines.flow.MutableStateFlow

data class GachaStringContentViewModelState(
    val state: GachaMachineState<String> = GachaMachineState(),
    val characterName: String = "",
    val placeHolder: PlaceholderParser = PlaceholderParser(""),
    val appearance: Appearance = Appearance(),
) {

    val action = state.action

    val status = state.status

    val title = state.title

    val errorMessage = state.errorMessage

    val isRolling
        get(): Boolean {
            return state.isRolling
        }

    val isComplete
        get(): Boolean {
            return state.isComplete
        }

    fun result(): SpannableStringBuilder {
        val r = state.result?.content ?: ""
        return placeHolder.parse(
            arrayListOf(characterName, r),
            Typeface.DEFAULT,
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        )
    }
}

class GachaStringContentViewModel(application: Application) : AndroidViewModel(application) {

    private val model = GachaMachine<String>()

    private val _state = MutableStateFlow(GachaStringContentViewModelState())

    val state = _state

    fun observe(owner: LifecycleOwner) {
        model.state.asLiveData().observe(owner) {
            _state.value = _state.value.copy(
                state = it ?: GachaMachineState()
            )
        }
    }

    fun setAppearance(appearance: Appearance) {
        _state.value = _state.value.copy(
            appearance = appearance
        )
    }

    fun setPlaceHolder(placeHolder: PlaceholderParser) {
        _state.value = _state.value.copy(
            placeHolder = placeHolder
        )
    }

    fun setCharacterName(name: String) {
        _state.value = _state.value.copy(
            characterName = name
        )
    }

    fun reset() {
        model.reset()
    }

    fun resetError() {
        model.resetError()
    }

    fun setAsset(asset: GachaItemAsset<String>) {
        model.setAsset(asset)
    }

    fun rollGacha() {
        model.rollGacha()
    }
}