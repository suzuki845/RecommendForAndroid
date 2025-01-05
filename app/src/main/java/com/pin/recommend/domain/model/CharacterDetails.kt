package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.google.gson.Gson
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.AnniversaryData
import com.pin.recommend.domain.entity.AnniversaryInterface
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.entity.ContentId
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.util.combine2
import com.pin.recommend.util.combine3
import java.util.Date
import java.util.LinkedList

class CharacterDetails(
    private val context: Context,
    private val accountModel: CharacterPinningManager,
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
        return@switchMap db.storyDao()
            .watchByCharacterIdStoryWithPictures(it.id, it.storySortOrder == 1)
    }

    private val account = accountModel.entity

    private val displayOnHomeAnniversary = _displayOnHomeAnniversaries.map {
        val a = it.firstOrNull()
        AnniversaryData(
            a?.getId() ?: ContentId.getEmpty(),
            a?.getName() ?: "",
            a?.getTopText() ?: "",
            a?.getBottomText() ?: "",
            a?.getElapsedDays(Date())?.let { d -> "${d}日" } ?: "",
            a?.getRemainingDays(Date())?.let { d -> "${d}日" } ?: "",
            a?.getMessage(Date()) ?: "",
            a?.isAnniversary(Date()) ?: false
        )
    }

    val state = combine3(account, cwa, displayOnHomeAnniversary) { a, b, c ->
        return@combine3 State(
            b?.id ?: -1,
            a?.fixedCharacterId != null,
            b?.character?.name ?: "",
            b?.appearance(context) ?: Appearance(),
            c ?: AnniversaryData(),
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
        AppDatabase.executor.execute {
            character?.let {
                db.recommendCharacterDao().updateCharacter(
                    it
                )
            }
        }
    }

    data class State(
        val characterId: Long,
        val isPinning: Boolean = false,
        val characterName: String,
        val appearance: Appearance,
        val anniversary: AnniversaryData,
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