package com.pin.recommend.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.pin.recommend.R
import com.pin.recommend.model.entity.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return EventRemoteViewsFactory(this.applicationContext, intent)
    }
}

class EventRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    val db = ContentWidgetDao(context)

    private val events = mutableListOf<Event>()

    private fun setData() {
        val list = db.unsafeGet(appWidgetId)?.getRecentEvents() ?: listOf()
        events.clear()
        events.addAll(list)
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
        return events.size
    }

    override fun getViewAt(i: Int): RemoteViews {
        val data = events[i]

        val rv = RemoteViews(context.packageName, R.layout.widget_event_item)
        rv.setTextViewText(R.id.name, data.title)
        rv.setTextViewText(R.id.date, formatDate(data.date))

        return rv
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("MM/dd (EEE)", Locale.getDefault())
        return formatter.format(date)
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