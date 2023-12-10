package com.pin.recommend.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture

class StoryListViewModel(application: Application) : AndroidViewModel(
    application
) {

    private val db = AppDatabase.getDatabase(application)

    fun deleteStory(story: Story) {
        AppDatabase.executor.execute {
            val storyPictures: List<StoryPicture> = db.storyPictureDao().findByStoryId(story.id)
            for (storyPicture in storyPictures) {
                storyPicture.deleteImage(getApplication())
            }
            db.storyDao().deleteStory(story)
        }
    }

}