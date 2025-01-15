package com.pin.recommend.ui.gacha

import android.app.Application
import android.graphics.Typeface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.domain.model.gacha.GachaItemAsset
import com.pin.recommend.domain.model.gacha.GachaMachine
import com.pin.recommend.domain.model.gacha.PlaceholderParser
import com.pin.recommend.ui.character.CharacterDetailsViewModelState

class GachaStringContentViewModel(application: Application) : AndroidViewModel(application) {

    private val model = GachaMachine<String>()

    private var state: CharacterDetailsViewModelState? = null

    private var placeHolder = PlaceholderParser("")

    fun setPlaceHolder(placeHolder: PlaceholderParser) {
        this.placeHolder = placeHolder
    }

    fun setCharacterDetailsViewModeState(state: CharacterDetailsViewModelState) {
        this.state = state
    }

    val title = model.title

    val isComplete = model.isComplete

    val isRolling = model.isRolling

    fun reset() {
        model.reset()
    }

    val result = model.result.map {
        val r = it?.content ?: ""
        placeHolder.parse(
            arrayListOf(state?.characterName ?: "", r),
            Typeface.DEFAULT,
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        )
    }

    fun setAsset(asset: GachaItemAsset<String>) {
        model.setAsset(asset)
    }

    fun rollGacha() {
        model.rollGacha()
    }
}