package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.entity.StoryWithPictures

class StoryDetailsViewModel(application: Application) : AndroidViewModel(
    application
) {
    private val db = AppDatabase.getDatabase(application)

    val id = MutableLiveData<Long?>()

    val story = id.switchMap {
        return@switchMap db.storyDao().watchByIdStoryWithPictures(it ?: -1)
    }

}