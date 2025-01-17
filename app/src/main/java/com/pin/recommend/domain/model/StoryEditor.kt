package com.pin.recommend.domain.model

import android.content.Context
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.util.BitmapUtility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

enum class StoryEditorAction {
    Init,
    Save
}

enum class StoryEditorStatus {
    Processing,
    Success,
    Failure
}

data class StoryEditorState(
    val action: StoryEditorAction = StoryEditorAction.Init,
    val status: StoryEditorStatus = StoryEditorStatus.Processing,
    val id: Long = 0,
    val characterId: Long? = null,
    val comment: String = "",
    val created: Date = Date(),
    val pictures: List<StoryPicture.Draft> = listOf(),
    val beforePictures: List<StoryPicture> = listOf(),
    val errorMessage: String? = null
) {
    val isNewEntity = id == 0L
}

class StoryEditor(val context: Context) {
    private val db = AppDatabase.getDatabase(context)

    private val _state = MutableStateFlow(StoryEditorState())

    val state: StateFlow<StoryEditorState> = _state

    fun setEntity(e: StoryWithPictures? = null) {
        _state.value = StoryEditorState(
            id = e?.id ?: 0L,
            characterId = e?.story?.characterId,
            created = e?.story?.created ?: Date(),
            comment = e?.story?.comment ?: "",
            pictures = e?.pictures?.map { it.toDraft(context) }?.toMutableList() ?: mutableListOf(),
            beforePictures = e?.pictures?.toList() ?: listOf()
        )
    }

    fun setCharacterId(characterId: Long) {
        _state.value = _state.value.copy(
            characterId = characterId
        )
    }

    fun setCreated(date: Date) {
        _state.value = _state.value.copy(
            created = date
        )
    }

    fun setComment(comment: String) {
        _state.value = _state.value.copy(comment = comment)
    }

    fun addPicture(p: StoryPicture.Draft) {
        val list = _state.value.pictures.toMutableList()
        list.add(p)
        _state.value = _state.value.copy(
            pictures = list
        )
    }

    fun replacePicture(p: StoryPicture.Draft) {
        val list = _state.value.pictures.toMutableList()
        val index = list.indexOfFirst { e -> e.uuid == p.uuid }
        if (index != -1) {
            list[index] = p
        }
        _state.value = _state.value.copy(
            pictures = list
        )
    }

    fun removePicture(pos: Int) {
        val list = _state.value.pictures.toMutableList()
        list.removeAt(pos)
        _state.value = _state.value.copy(
            pictures = list
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun save() {
        try {
            _state.value = _state.value.copy(
                action = StoryEditorAction.Save,
                status = StoryEditorStatus.Processing
            )
            val s = _state.value
            val id = s.id
            val entity = Story()
            entity.id = id
            entity.characterId = s.characterId ?: throw Exception("missing character id")
            if (entity.characterId == -1L) {
                throw Exception("invalid character id")
            }
            entity.comment = s.comment
            entity.created = s.created

            if (id == 0L) {
                val newId = db.storyDao().insertStory(entity)
                s.pictures.forEach { picture ->
                    val bitmap = picture.bitmap
                    val filename = BitmapUtility.generateFilename()
                    val ext = ".png"
                    if (BitmapUtility.insertPrivateImage(context, bitmap, filename, ext)) {
                        val uri = filename + ext
                        val persist = StoryPicture()
                        persist.uri = uri
                        persist.storyId = newId
                        db.storyPictureDao().insertStoryPicture(persist)
                    }

                }
            } else {
                db.storyDao().updateStory(entity)
                s.pictures.forEach { picture ->
                    val bitmap = picture.bitmap
                    val filename = BitmapUtility.generateFilename()
                    val ext = ".png"
                    if (BitmapUtility.insertPrivateImage(context, bitmap, filename, ext)) {
                        val uri = filename + ext
                        val persist = StoryPicture()
                        persist.uri = uri
                        persist.storyId = entity.id
                        db.storyPictureDao().insertStoryPicture(persist)
                    }
                }


                s.beforePictures.forEach { picture ->
                    picture.deleteImage(context)
                    db.storyPictureDao().deleteStoryPicture(picture)
                }
            }

            _state.value = _state.value.copy(
                action = StoryEditorAction.Save,
                status = StoryEditorStatus.Success
            )

        } catch (e: Exception) {
            println("StoryEdit!! ${e.stackTrace.contentToString()}")
            _state.value = _state.value.copy(
                action = StoryEditorAction.Save,
                status = StoryEditorStatus.Failure,
                errorMessage = e.message
            )
        }
    }


}