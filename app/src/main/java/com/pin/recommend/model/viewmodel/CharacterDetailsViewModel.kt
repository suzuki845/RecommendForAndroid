package com.pin.recommend.model.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.ScreenShotActivity
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.entity.Account
import java.util.*

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDetails: CharacterDetails

    init{
        val characterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
        val accountDao = AppDatabase.getDatabase(application).accountDao()
        characterDetails = CharacterDetails(application, accountDao, characterDao)
        characterDetails.initialize()
    }

    val state = characterDetails.state

    val character = characterDetails.cwa.map { it?.character }

    val id = characterDetails.id

    val icon = state.map { it.appearance.iconImage }

    fun changeAnniversary(){
        characterDetails.changeDisplayOnHomeAnniversary()
    }

    fun pinning(){
        characterDetails.pinning()
    }

    fun unpinning(){
        characterDetails.unpinning()
    }

    fun updateStorySortOrder(order: Int) {
        characterDetails.updateStorySortOrder(order)
    }

}