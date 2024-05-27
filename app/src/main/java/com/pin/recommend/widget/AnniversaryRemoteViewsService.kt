package com.pin.recommend.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.pin.recommend.R
import com.pin.recommend.widget.AnniversaryWidgetProvider.Companion.ACTION_ITEM_CLICK
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date


class AnniversaryRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return AnniversaryRemoteViewsFactory(this.applicationContext, intent)
    }
}

class AnniversaryRemoteViewsFactory(context: Context, intent: Intent) : RemoteViewsFactory {

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    val db = AnniversaryWidgetDao(context)

    private val ctx = context

    val anniversaries = mutableListOf<AnniversaryWidgetItem>()

    override fun onCreate() {
        GlobalScope.launch {
            val entries = db.unsafeCharacters().flatMap { cwa ->
                cwa.anniversaries().map { anniversary ->
                    return@map AnniversaryWidgetItem(
                        cwa.character.name ?: "",
                        anniversary.toData(Date()),
                        cwa.serializableAppearance()
                    )
                }
            }
            anniversaries.clear()
            anniversaries.addAll(entries)
            onDataSetChanged()
            //println("test!!$entries")
        }

        /*
        db.characters().observeForever {
            val entries = it.flatMap { cwa ->
                cwa.anniversaries().map { anniversary ->
                    return@map AnniversaryWidgetItem(
                        cwa.character.name ?: "",
                        anniversary.toData(Date()),
                        cwa.serializableAppearance()
                    )
                }
            }

            anniversaries.clear()
            anniversaries.addAll(entries)
            onDataSetChanged()
        }
         */
    }

    override fun onDataSetChanged() {
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return anniversaries.size
    }

    override fun getViewAt(i: Int): RemoteViews {

        val data = anniversaries[i]

        val pinRequest = Intent(ctx, AnniversaryWidgetProvider::class.java)
        pinRequest.action = AnniversaryWidgetProvider.ACTION_UPDATE
        pinRequest.putExtra(ACTION_ITEM_CLICK, PinnedAnniversaryWidget(appWidgetId, data).toJson())

        val rv = RemoteViews(ctx.packageName, R.layout.widget_anniversary_item)
        rv.setTextViewText(R.id.character_name, data.characterName)
        rv.setTextViewText(R.id.anniversary_name, data.anniversary.name + "記念")
        rv.setImageViewBitmap(
            R.id.character_icon,
            anniversaries[i].appearance.getIconImage(ctx, 30, 30)
        )
        rv.setOnClickFillInIntent(R.id.item_container, pinRequest)

        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}