package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterDetails
import java.util.*

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDetails: CharacterDetails

    init{
        val characterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
        val customAnniversaryDao = AppDatabase.getDatabase(application).customAnniversaryDao()

        characterDetails = CharacterDetails(application, characterDao, customAnniversaryDao)
        characterDetails.initialize()
    }

    val state = characterDetails.state

    fun setId(id: Long?){
        characterDetails.setId(id)
    }

    fun changeAnniversary(){
        characterDetails.changeDisplayOnHomeAnniversary()
    }


}