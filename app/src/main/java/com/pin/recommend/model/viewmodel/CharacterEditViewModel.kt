package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.model.CharacterEditModel
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.entity.RecommendCharacter

class CharacterEditViewModel(application: Application) : AndroidViewModel(application) {

    private val model = CharacterEditModel(application)

    val name = model.name

    val created = model.created

    val iconImage = model.iconImage

    val iconWithDefaultImage = model.iconWithDefaultImage

    val backgroundImage = model.backgroundImage

    val backgroundColor = model.backgroundColor

    val homeTextColor = model.homeTextColor

    val aboveText = model.aboveText

    val belowText = model.belowText

    val isZeroDayStart = model.isZeroDayStart

    val elapsedDateFormat = model.elapsedDateFormat

    val fontFamily = model.fontFamily

    val typeface = model.typeface

    val backgroundImageOpacity = model.backgroundImageOpacity

    val homeTextShadowColor = model.homeTextShadowColor

    val anniversaries = model.anniversaries

    fun initialize(id: Long){
        model.initialize(id)
    }

    fun addAnniversary(anniversary: CustomAnniversary.Draft){
        model.addAnniversary(anniversary)
    }

    fun replaceAnniversary(anniversary: CustomAnniversary.Draft){
        model.replaceAnniversary(anniversary)
    }

    fun update(character: RecommendCharacter) {

    }

}