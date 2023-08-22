package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.dao.StoryDao
import com.pin.recommend.model.dao.StoryPictureDao
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter

class RecommendCharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao: RecommendCharacterDao
    private val storyDao: StoryDao
    private val storyPictureDao: StoryPictureDao
    private var character: LiveData<RecommendCharacter?>? = null

    init {
        characterDao = AppDatabase.getDatabase(application).recommendCharacterDao()
        storyDao = AppDatabase.getDatabase(application).storyDao()
        storyPictureDao = AppDatabase.getDatabase(application).storyPictureDao()
    }


    fun getCharacters(accountLiveData: LiveData<Account>): LiveData<List<RecommendCharacter>> {
        return Transformations.switchMap<Account, List<RecommendCharacter>>(
            accountLiveData,
            object : Function<Account?, LiveData<List<RecommendCharacter>>> {
                override fun apply(input: Account?): LiveData<List<RecommendCharacter>> {
                    if(input == null) return MutableLiveData(listOf())
                    return characterDao.watchByAccountId(input.id)
                }
            })
    }

    fun getCharacter(characterId: Long?): LiveData<RecommendCharacter?> {
        if (character == null) {
            character = characterDao.watchById(characterId!!)
        }
        return character!!
    }

    fun insert(character: RecommendCharacter?) {
        AppDatabase.executor.execute { characterDao.insertCharacter(character) }
    }

    fun update(character: RecommendCharacter?) {
        AppDatabase.executor.execute { characterDao.updateCharacter(character) }
    }

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