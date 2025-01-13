package com.pin.recommend.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.StoryWithPictures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class StoryDetailsViewModelAction {
    Init,
    SetEntity,
}

enum class StoryDetailsViewModelStatus {
    Processing,
    Success,
    Failure
}

data class StoryDetailsViewModelState(
    val action: StoryDetailsViewModelAction = StoryDetailsViewModelAction.Init,
    val status: StoryDetailsViewModelStatus = StoryDetailsViewModelStatus.Processing,
    val storyWithPictures: StoryWithPictures? = null,
    val errorMessage: String? = null
) {
    val id = storyWithPictures?.story?.id
    val characterId = storyWithPictures?.story?.characterId
    val formattedDate = storyWithPictures?.story?.formattedDate
    val comment = storyWithPictures?.story?.comment
    val pictures = storyWithPictures?.pictures
}

class StoryDetailsViewModel(application: Application) : AndroidViewModel(
    application
) {
    private val db = AppDatabase.getDatabase(application)

    private val _state = MutableStateFlow(StoryDetailsViewModelState())

    val state: Flow<StoryDetailsViewModelState> = _state

    fun subscribe(owner: LifecycleOwner) {
        val id = _state.value.storyWithPictures?.id ?: -1L
        db.storyDao().watchByIdStoryWithPictures(id).observe(owner) {
            _state.value = _state.value.copy(storyWithPictures = it)
        }
    }

    fun setEntity(e: StoryWithPictures?) {
        _state.value = StoryDetailsViewModelState(
            action = StoryDetailsViewModelAction.SetEntity,
            status = StoryDetailsViewModelStatus.Success,
            e
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

}