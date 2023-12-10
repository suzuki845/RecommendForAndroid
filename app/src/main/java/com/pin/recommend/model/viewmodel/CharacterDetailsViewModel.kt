package com.pin.recommend.model.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.pin.recommend.ScreenShotActivity
import com.pin.recommend.model.AccountModel
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.Story
import java.util.*

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDetails: CharacterDetails
    private val accountModel = AccountModel(application)

    init{
        characterDetails = CharacterDetails(application, accountModel)
        characterDetails.initialize()
    }

    val state = characterDetails.state

    val character = characterDetails.character

    val stories = characterDetails.stories

    val id = characterDetails.id

    val account = accountModel.entity

    fun deleteStory(story: Story) {
        characterDetails.deleteStory(story)
    }

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