package com.pin.recommend.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.google.gson.Gson
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.dao.CustomAnniversaryDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.dao.StoryDao
import com.pin.recommend.model.entity.*
import com.pin.recommend.util.combine2
import com.pin.recommend.util.combine3
import java.util.*

class CharacterDetails(
    private val context: Context,
    private val accountModel: AccountModel,
) {

    private val db = AppDatabase.getDatabase(context)

    val id = MutableLiveData<Long?>()

    private val _displayOnHomeAnniversaries = MutableLiveData<List<AnniversaryInterface>>(listOf())

    val cwa = combine2(id, accountModel.entity) { id, account ->
        return@combine2 db.recommendCharacterDao().watchByIdCharacterWithAnniversaries(
            (id ?: account?.fixedCharacterId) ?: -1
        )
    }.switchMap { it }

    val character = cwa.map {
        it?.character
    }

    val stories = character.switchMap {
        if (it == null) return@switchMap MutableLiveData(listOf())
        return@switchMap db.storyDao().watchByCharacterIdStoryWithPictures(it.id, it.storySortOrder == 1)
    }

    private val account = accountModel.entity

    private val displayOnHomeAnniversary = _displayOnHomeAnniversaries.map {
        val a = it.firstOrNull()
        Anniversary(
            a?.getName() ?: "",
            a?.getTopText() ?: "",
            a?.getBottomText() ?: "",
            a?.getElapsedDays(Date())?.let { d -> "${d}日" } ?: "",
            a?.getMessage(Date()) ?: ""
        )
    }

    val state = combine3(account, cwa, displayOnHomeAnniversary) { a, b, c ->
        return@combine3 State(
            b?.id ?: -1,
            a?.fixedCharacterId != null,
            b?.character?.name ?: "",
            b?.appearance(context) ?: Appearance(),
            c ?: Anniversary(),
            b?.character?.storySortOrder ?: 0
        )
    }

    fun initialize() {
        cwa.observeForever {
            _displayOnHomeAnniversaries.value = it?.anniversaries()
        }
    }

    fun changeDisplayOnHomeAnniversary() {
        val current = LinkedList<AnniversaryInterface>()
        _displayOnHomeAnniversaries.value?.forEach {
            current.add(it)
        }
        current.addFirst(current.removeLast())
        _displayOnHomeAnniversaries.value = current
    }

    fun deleteStory(story: Story) {
        AppDatabase.executor.execute {
            val storyPictures: List<StoryPicture> = db.storyPictureDao().findByStoryId(story.id)
            for (storyPicture in storyPictures) {
                storyPicture.deleteImage(context)
            }
            db.storyDao().deleteStory(story)
        }
    }

    fun pinning() {
        id.value?.let {
            accountModel.pinning(it)
        }
    }

    fun unpinning() {
        accountModel.unpinning()
    }

    fun updateStorySortOrder(order: Int) {
        val character = cwa.value?.character
        character?.storySortOrder = order
        AppDatabase.executor.execute { db.recommendCharacterDao().updateCharacter(character) }
    }

    data class State(
        val characterId: Long,
        val isPinning: Boolean = false,
        val characterName: String,
        val appearance: Appearance,
        val anniversary: Anniversary,
        val storySortOrder: Int,
    ) {
        fun toJson(): String {
            return Gson().toJson(this)
        }

        companion object {
            fun fromJson(json: String): State {
                return Gson().fromJson(json, State::class.java)
            }
        }
    }

}