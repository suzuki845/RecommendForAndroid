package com.pin.recommend.widget

import android.content.Context
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.CharacterWithRelations
import com.pin.recommend.util.PrefUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentWidgetDao(context: Context) {
    private val db = AppDatabase.getDatabase(context)

    private val pref by lazy { PrefUtil(context) }

    fun unsafeCharacters(): List<CharacterWithRelations> {
        return db.recommendCharacterDao().findCharacterWithRelationsAndRecentEvents()
    }

    fun pinning(widgetId: Int, item: ContentWidgetItem) {
        pref.putString(
            widgetId.toString(),
            PinnedContentWidget(widgetId, item).toJson()
        )
    }

    fun unpinning(widgetId: Int) {
        pref.remove(widgetId.toString())
    }

    private fun getPinned(widgetId: Int): PinnedContentWidget? {
        val json = pref.getString(widgetId.toString())
        return PinnedContentWidget.fromJson(json)
    }

    suspend fun get(widgetId: Int): DisplayContentWidget? = withContext(Dispatchers.IO) {
        val pinned = getPinned(widgetId) ?: return@withContext null
        val cwr = db.recommendCharacterDao()
            .findByIdCharacterWithRelations(
                pinned.content.getId().getCharacterId()
            )
        val a = cwr?.typedEntities()?.firstOrNull {
            pinned.getId().getId() == it.id.getId()
        }
        if (a != null) {
            return@withContext DisplayContentWidget(
                pinned.widgetId,
                cwr.character.name ?: "",
                a,
                cwr.serializableAppearance()
            )
        }
        return@withContext null
    }

    fun unsafeGet(widgetId: Int): DisplayContentWidget? {
        val pinned = getPinned(widgetId) ?: return null
        val cwr = db.recommendCharacterDao()
            .findByIdCharacterWithRelationsAndRecentEvents(
                pinned.content.getId().getCharacterId()
            )
        val content = cwr?.typedEntities()?.firstOrNull {
            pinned.getId().getId() == it.id.getId()
        }
        if (content != null) {
            return DisplayContentWidget(
                pinned.widgetId,
                cwr.character.name ?: "",
                content,
                cwr.serializableAppearance()
            )
        }
        return null
    }
}

