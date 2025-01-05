package com.pin.recommend.ui.anniversary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.model.AnniversaryEditor

class AnniversaryEditorViewModel(application: Application) : AndroidViewModel(application) {

    val model = AnniversaryEditor()

    val characterId = model.characterId
    val name = model.name
    val date = model.date
    val topText = model.topText
    val bottomText = model.bottomText

    fun save(onComplete: (CustomAnniversary.Draft) -> Unit, onError: (Exception) -> Unit) {
        model.save(onComplete, onError)
    }

    fun initialize(e: CustomAnniversary? = null) {
        model.initialize(e)
    }

    fun initialize(e: CustomAnniversary.Draft? = null) {
        model.initialize(e)
    }

}