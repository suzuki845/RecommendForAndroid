package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.gacha.BadgeGachaMachine

class BadgeGachaViewModel(application: Application) : AndroidViewModel(application) {
    private val model = BadgeGachaMachine()

    private var characterDetailsState: CharacterDetails.State? = null

    fun setCharacterDetailsState(state: CharacterDetails.State) {
        this.characterDetailsState = state
    }

    val title = model.title

    val isComplete = model.isComplete

    val isRolling = model.isRolling

    fun reset() {
        model.reset()
    }

    val resultImage = model.result.map { it?.content }

    val resultMessage = model.result.map {
        return@map if (it?.name == "Prize") "アタリ" else "ハズレ"
    }

    val result = model.result

    fun rollGacha() {
        model.rollGacha()
    }
}