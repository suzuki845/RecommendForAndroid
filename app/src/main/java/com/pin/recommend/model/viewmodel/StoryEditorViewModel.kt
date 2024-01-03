package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.model.StoryEditor
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.entity.StoryWithPictures
import com.pin.recommend.util.Progress

class StoryEditorViewModel(application: Application) : AndroidViewModel(application) {

    val model = StoryEditor(application)
    val id = model.id
    val characterId = model.characterId
    val comment = model.comment
    val created = model.created
    val pictures = model.pictures

    fun initialize(e: StoryWithPictures? = null) {
        model.initialize(e)
    }

    fun addPicture(p: StoryPicture.Draft) {
        model.addPicture(p)
    }

    fun replacePicture(p: StoryPicture.Draft) {
        model.replacePicture(p)
    }

    fun removePicture(pos: Int) {
        model.removePicture(pos)
    }

    fun save(p: Progress) {
        model.save(p)
    }

}