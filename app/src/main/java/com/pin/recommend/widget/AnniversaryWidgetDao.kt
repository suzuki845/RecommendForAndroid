package com.pin.recommend.widget

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.util.PrefUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnniversaryWidgetDao(context: Context) {
    private val db = AppDatabase.getDatabase(context)

    fun characters(): LiveData<List<CharacterWithAnniversaries>> {
        return db.recommendCharacterDao().watchCharacterWithAnniversaries()
    }

    fun unsafeCharacters(): List<CharacterWithAnniversaries> {
        return db.recommendCharacterDao().findCharacterWithAnniversaries()
    }

    fun register(widgetId: Int, item: AnniversaryWidgetItem) {
        PrefUtil.putString(
            widgetId.toString(),
            PinnedAnniversaryWidget(widgetId, item).toJson()
        )
    }

    fun remove(widgetId: Int) {
        PrefUtil.remove(widgetId.toString())
    }

    private fun getPinned(widgetId: Int): PinnedAnniversaryWidget? {
        val json = PrefUtil.getString(widgetId.toString())
        return PinnedAnniversaryWidget.fromJson(json)
    }

    fun watch(widgetId: Int): LiveData<DisplayAnniversaryWidget?> {
        val pinned = getPinned(widgetId) ?: return MutableLiveData()
        return db.recommendCharacterDao()
            .watchByIdCharacterWithAnniversaries(pinned.anniversary.getId().getCharacterId())
            .map { cwa ->
                val a = cwa?.anniversaries()?.firstOrNull {
                    pinned.getId().getId() == it.getId().getId()
                }
                //println("widget!! ${cwa?.id}")
                if (a != null) {
                    return@map DisplayAnniversaryWidget(
                        pinned.widgetId,
                        pinned.getCharacterName(),
                        a,
                        pinned.getAppearance()
                    )
                }
                return@map null
            }
    }

    suspend fun get(widgetId: Int): DisplayAnniversaryWidget? = withContext(Dispatchers.IO) {
        val pinned = getPinned(widgetId) ?: return@withContext null
        val cwa = db.recommendCharacterDao()
            .findByIdCharacterWithAnniversaries(
                pinned.anniversary.getId().getCharacterId()
            )
        val a = cwa?.anniversaries()?.firstOrNull {
            pinned.getId().getId() == it.getId().getId()
        }
        println("widget!! ${cwa?.id}")
        if (a != null) {
            return@withContext DisplayAnniversaryWidget(
                pinned.widgetId,
                cwa.character.name ?: "",
                a,
                cwa.serializableAppearance()
            )
        }
        return@withContext null
    }
}

