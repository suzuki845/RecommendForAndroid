package com.pin.recommend.model.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.model.CharacterEditor
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.util.Progress

class CharacterEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val model = CharacterEditor(application)

    val name = model.name

    val created = model.created

    val iconImage = model.iconImage

    val iconWithDefaultImage = model.iconWithDefaultImage

    val backgroundImage = model.backgroundImage

    val backgroundColor = model.backgroundColor

    val backgroundColorToBitmap = model.backgroundColorToBitmap

    val homeTextColor = model.homeTextColor

    val homeTextColorToBitmap = model.homeTextColorToBitmap

    val homeTextShadowColor = model.homeTextShadowColor

    val homeTextShadowColorToBitmap = model.homeTextShadowColorToBitmap

    val aboveText = model.aboveText

    val belowText = model.belowText

    val isZeroDayStart = model.isZeroDayStart

    val elapsedDateFormat = model.elapsedDateFormat

    val fontFamily = model.fontFamily

    val typeface = model.typeface

    val backgroundImageOpacity = model.backgroundImageOpacity

    val isVisibleBackgroundImageOpacityView = backgroundImage.map {
        if (it != null) View.VISIBLE else View.GONE
    }

    val anniversaries = model.anniversaries

    fun initialize(id: Long) {
        model.initialize(id)
    }

    fun initialize(entity: CharacterWithAnniversaries?) {
        model.initialize(entity)
    }

    fun addAnniversary(anniversary: CustomAnniversary.Draft) {
        model.addAnniversary(anniversary)
    }

    fun replaceAnniversary(anniversary: CustomAnniversary.Draft) {
        model.replaceAnniversary(anniversary)
    }

    fun removeAnniversary(pos: Int) {
        model.removeAnniversary(pos)
    }

    fun save(p: Progress) {
        model.save(p)
    }

}