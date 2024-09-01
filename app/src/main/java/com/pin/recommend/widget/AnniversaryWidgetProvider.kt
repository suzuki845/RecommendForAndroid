package com.pin.recommend.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.pin.recommend.MainActivity
import com.pin.recommend.R
import java.util.Date
import java.util.Random


/**
 * Implementation of App Widget functionality.
 */


class AnniversaryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.anniversary_list);

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val db = AnniversaryWidgetDao(context)
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)

        for (appWidgetId in appWidgetIds) {
            db.remove(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        println("widget!!onReceive ${intent.action}")
        when (intent.action) {
            "android.appwidget.action.APPWIDGET_UPDATE" -> {

                val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
                val ids = widgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        AnniversaryWidgetProvider::class.java
                    )
                )

                for (appWidgetId in ids) {
                    updateAppWidget(context, widgetManager, appWidgetId)
                }
                widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.anniversary_list);
            }

            ACTION_ITEM_CLICK -> {
                val db = AnniversaryWidgetDao(context)
                val json =
                    intent.getStringExtra(ACTION_ITEM_CLICK) ?: ""
                val piningItem = PinnedAnniversaryWidget.fromJson(json)
                if (piningItem != null) {
                    db.register(piningItem.widgetId, piningItem.anniversary)
                    val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
                    updateAppWidget(context, widgetManager, piningItem.widgetId)
                }
            }

            ACTION_UNPINNING -> {
                val appWidgetId =
                    intent.getIntExtra(ACTION_UNPINNING, -1)
                val db = AnniversaryWidgetDao(context)
                db.remove(appWidgetId)

                val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
                val ids = widgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        AnniversaryWidgetProvider::class.java
                    )
                )
                for (appWidgetId in ids) {
                    widgetManager.updateAppWidget(appWidgetId, getListView(context, appWidgetId))
                }
                widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.anniversary_list);
            }
        }

        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_UPDATE =
            "com.pin.recommend.AnniversaryWidgetProvider.ACTION_UPDATE"
        const val ACTION_ITEM_CLICK =
            "com.pin.recommend.AnniversaryWidgetProvider.ACTION_ITEM_CLICK"
        const val ACTION_UNPINNING = "com.pin.recommend.AnniversaryWidgetProvider.ACTION_UNPINNING"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val db = AnniversaryWidgetDao(context)
            val a = db.unsafeGet(appWidgetId)
            val views = if (a != null) {
                getDisplayView(context, appWidgetId, a)
            } else {
                getListView(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getListView(
            context: Context,
            appWidgetId: Int
        ): RemoteViews {
            val serviceIntent = Intent(context, AnniversaryRemoteViewsService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.putExtra("random", Random().nextInt())
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context.packageName, R.layout.widget_anniversary_list)
            views.setRemoteAdapter(R.id.anniversary_list, serviceIntent)
            val itemClickIntent = Intent(context, AnniversaryWidgetProvider::class.java)
            itemClickIntent.action = ACTION_ITEM_CLICK
            val itemClickPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                itemClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.anniversary_list, itemClickPendingIntent)

            return views
        }

        private fun getDisplayView(
            context: Context,
            appWidgetId: Int,
            a: DisplayAnniversaryWidget
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_display_anniversary)
            views.setImageViewBitmap(
                R.id.background_image,
                a.getIconImage(context, 500, 500)
            )
            views.setInt(R.id.background_color, "setBackgroundColor", a.getBackgroundColor())
            views.setTextViewText(R.id.character_name, a.characterName)
            views.setTextViewText(
                R.id.remaining_days,
                a.getMessage(Date())
            )
            val pinDrawable = context.getDrawable(
                R.drawable.pin_fill
            )
            views.setTextColor(R.id.character_name, a.getTextColor())
            views.setTextColor(R.id.anniversary_name, a.getTextColor())
            views.setTextColor(R.id.remaining_days, a.getTextColor())
            pinDrawable?.setTint(a.getTextColor())

            a.getTextShadowColor()?.let {
                views.setInt(R.id.character_name_background, "setColorFilter", it)
                views.setInt(R.id.anniversary_background, "setColorFilter", it)
                views.setInt(R.id.unpinning_background, "setColorFilter", it)
            }

            views.setImageViewBitmap(R.id.unpinning, pinDrawable?.toBitmap(30, 30))

            val intent = Intent(context, AnniversaryWidgetProvider::class.java)
            intent.action = ACTION_UNPINNING
            intent.putExtra(ACTION_UNPINNING, appWidgetId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setOnClickPendingIntent(R.id.unpinning, pIntent)

            val openAppIntent = Intent(
                context,
                MainActivity::class.java
            )
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setOnClickPendingIntent(R.id.container, pendingIntent)

            return views
        }
    }

}
