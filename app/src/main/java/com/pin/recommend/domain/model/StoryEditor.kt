package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.util.Progress
import java.util.Date

class StoryEditor(val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    val id = MutableLiveData<Long?>(null)
    val characterId = MutableLiveData<Long?>(null)
    val comment = MutableLiveData<String?>()
    val created = MutableLiveData(Date())
    val pictures = MutableLiveData(mutableListOf<StoryPicture.Draft>())
    private val beforePictures = MutableLiveData(listOf<StoryPicture>())

    fun initialize(e: StoryWithPictures? = null) {
        id.value = e?.id
        characterId.value = e?.story?.characterId
        created.value = e?.story?.created
        comment.value = e?.story?.comment
        pictures.value = e?.pictures?.map { it.toDraft(context) }?.toMutableList()
        beforePictures.value = e?.pictures?.toList()
    }

    fun addPicture(p: StoryPicture.Draft) {
        val list = pictures.value ?: mutableListOf()
        list.add(p)
        pictures.value = list
    }

    fun replacePicture(p: StoryPicture.Draft) {
        val list = pictures.value ?: mutableListOf()
        val index = list.indexOfFirst { e -> e.uuid == p.uuid }
        if (index != -1) {
            list[index] = p
        }
        pictures.value = list
    }

    fun removePicture(pos: Int) {
        val items = pictures.value ?: mutableListOf()
        items.removeAt(pos)
        pictures.value = items
    }

    fun save(p: Progress) {
        try {
            p.onStart()
            val id = id.value ?: 0L
            val entity = Story()
            entity.id = id
            entity.characterId = characterId.value ?: throw Exception("missing character id")
            if (entity.characterId == -1L) {
                throw Exception("invalid character id")
            }
            entity.comment = comment.value
            entity.created = created.value

            if (id == 0L) {
                val newId = db.storyDao().insertStory(entity)
                pictures.value?.let { pictures ->
                    pictures.forEach { picture ->
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
                }
            } else {
                pictures.value?.let { pictures ->
                    pictures.forEach { picture ->
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
                }

                beforePictures.value?.let { pictures ->
                    pictures.forEach { picture ->
                        picture.deleteImage(context)
                        db.storyPictureDao().deleteStoryPicture(picture)
                    }
                }

                db.storyDao().updateStory(entity)
            }
            p.onComplete()
        } catch (e: Exception) {
            p.onError(e)
        }
    }


}