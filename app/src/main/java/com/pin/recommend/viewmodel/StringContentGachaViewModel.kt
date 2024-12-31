package com.pin.recommend.viewmodel

import android.app.Application
import android.graphics.Typeface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.gacha.GachaItemAsset
import com.pin.recommend.model.gacha.GachaMachine
import com.pin.recommend.util.PlaceholderParser

class StringContentGachaViewModel(application: Application) : AndroidViewModel(application) {

    private val model = GachaMachine<String>()

    private var characterDetailsState: CharacterDetails.State? = null

    private var placeHolder = PlaceholderParser("")

    fun setCharacterDetailsState(state: CharacterDetails.State) {
        this.characterDetailsState = state
    }

    fun setPlaceHolder(placeHolder: PlaceholderParser) {
        this.placeHolder = placeHolder
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
            arrayListOf(characterDetailsState?.characterName ?: "", r),
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