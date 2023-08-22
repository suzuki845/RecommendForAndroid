package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.model.AnniversaryEditModel
import com.pin.recommend.model.entity.CustomAnniversary

class AnniversaryEditViewModel(application: Application) : AndroidViewModel(application) {

    val model = AnniversaryEditModel()

    val characterId = model.characterId
    val name = model.name
    val date = model.date
    val topText = model.topText
    val bottomText = model.bottomText

    fun save(onComplete: (CustomAnniversary.Draft) -> Unit){
        model.save(onComplete)
    }

    fun initialize(e: CustomAnniversary? = null){
        model.initialize(e)
    }

}