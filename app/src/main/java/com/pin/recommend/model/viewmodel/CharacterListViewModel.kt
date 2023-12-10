package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.dao.StoryDao
import com.pin.recommend.model.dao.StoryPictureDao
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter

class CharacterListViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao: RecommendCharacterDao
    private val storyDao: StoryDao
    private val storyPictureDao: StoryPictureDao

    init {
        characterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
        storyDao = AppDatabase.getDatabase(application).storyDao()
        storyPictureDao = AppDatabase.getDatabase(application).storyPictureDao()
    }

    val characters = characterDao.watch()

    fun delete(character: RecommendCharacter) {
        AppDatabase.executor.execute {
            character.deleteIconImage(getApplication())
            character.deleteBackgroundImage(getApplication())
            val stories = storyDao.findByCharacterId(character.id)
            for (story in stories) {
                val storyPictures = storyPictureDao.findByStoryId(story.id)
                for (storyPicture in storyPictures) {
                    storyPicture.deleteImage(getApplication())
                }
            }
            characterDao.deleteCharacter(character)
        }
    }
}