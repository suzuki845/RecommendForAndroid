package com.pin.recommend.model.viewmodel

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.R
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.gacha.BadgeGachaMachine

class BadgeGachaViewModel(application: Application) : AndroidViewModel(application) {
    private val model = BadgeGachaMachine(AppDatabase.getDatabase(application))

    private var characterDetailsState: CharacterDetails.State? = null

    fun setCharacterDetailsState(state: CharacterDetails.State) {
        this.characterDetailsState = state
        model.setPrizeImage(state.appearance.iconImage)
    }

    val title = model.title

    val isComplete = model.isComplete

    val isRolling = model.isRolling

    val summary = model.summary

    val result = model.result

    val characterId = model.characterId

    fun reset() {
        model.reset()
    }

    val notPrizeImage = BitmapFactory.decodeResource(
        application.resources,
        R.drawable.ic_person_300dp
    )

    val resultImage = model.result.map {
        if (it?.name == "Prize") {
            it?.content
        } else notPrizeImage
    }

    val resultMessage = model.result.map {
        return@map if (it?.name == "Prize") "アタリ!" else "ハズレ・・・"
    }

    fun rollGacha() {
        model.rollGacha()
    }
}