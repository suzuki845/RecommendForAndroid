package com.pin.recommend.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.pin.recommend.R
import com.pin.recommend.widget.ContentWidgetProvider.Companion.ACTION_ITEM_CLICK


class ContentRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ContentRemoteViewsFactory(this.applicationContext, intent)
    }
}

class ContentRemoteViewsFactory(context: Context, intent: Intent) : RemoteViewsFactory {

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    val db = ContentWidgetDao(context)

    private val ctx = context

    val anniversaries = mutableListOf<ContentWidgetItem>()

    private fun setData() {
        val entries = db.unsafeCharacters().flatMap { cwr ->
            cwr.typedEntities().map {
                return@map ContentWidgetItem(
                    cwr.character.name ?: "",
                    it,
                    cwr.serializableAppearance()
                )
            }
        }
        anniversaries.clear()
        anniversaries.addAll(entries)
    }

    override fun onCreate() {
        setData()
    }

    override fun onDataSetChanged() {
        setData()
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return anniversaries.size
    }

    override fun getViewAt(i: Int): RemoteViews {

        val data = anniversaries[i]

        val pinRequest = Intent(ctx, ContentWidgetProvider::class.java)
        pinRequest.action = ContentWidgetProvider.ACTION_UPDATE
        pinRequest.putExtra(ACTION_ITEM_CLICK, PinnedContentWidget(appWidgetId, data).toJson())

        val rv = RemoteViews(ctx.packageName, R.layout.widget_content_item)
        rv.setTextViewText(R.id.character_name, data.characterName)
        rv.setTextViewText(R.id.anniversary_name, data.content.name)
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