package com.pin.recommend.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.domain.model.StoryEditor
import com.pin.recommend.domain.model.StoryEditorState
import kotlinx.coroutines.flow.map
import java.util.Date

class StoryEditorViewModelState(state: StoryEditorState = StoryEditorState()) {
    val action = state.action
    val status = state.status
    val id = state.id
    val characterId = state.characterId
    val comment = state.comment
    val created = state.created
    val pictures = state.pictures
    val beforePictures = state.beforePictures
    val isNewEntity = state.isNewEntity
    val errorMessage = state.errorMessage
}

class StoryEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val model = StoryEditor(application)

    val state = model.state.map { StoryEditorViewModelState(it) }

    fun setEntity(e: StoryWithPictures? = null) {
        model.setEntity(e)
    }

    fun setCharacterId(characterId: Long) {
        model.setCharacterId(characterId)
    }

    fun setCreated(date: Date) {
        model.setCreated(date)
    }

    fun setComment(comment: String) {
        model.setComment(comment)
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

    fun resetError() {
        model.resetError()
    }

    fun save() {
        model.save()
    }

}