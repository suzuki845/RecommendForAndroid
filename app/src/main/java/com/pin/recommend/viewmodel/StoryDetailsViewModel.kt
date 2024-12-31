package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.pin.recommend.model.AppDatabase

class StoryDetailsViewModel(application: Application) : AndroidViewModel(
    application
) {
    private val db = AppDatabase.getDatabase(application)

    val id = MutableLiveData<Long?>()

    val story = id.switchMap {
        return@switchMap db.storyDao().watchByIdStoryWithPictures(it ?: -1)
    }

}