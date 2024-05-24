package com.pin.recommend.widget

import android.content.Context
import androidx.lifecycle.LiveData
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.util.PrefUtil

class AnniversaryWidgetDao(context: Context) {
    private val db = AppDatabase.getDatabase(context)

    fun characters(): LiveData<List<CharacterWithAnniversaries>> {
        return db.recommendCharacterDao().watchCharacterWithAnniversaries()
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

    fun get(widgetId: Int): PinnedAnniversaryWidget? {
        val json = PrefUtil.getString(widgetId.toString())
        return PinnedAnniversaryWidget.fromJson(json)
    }

}

