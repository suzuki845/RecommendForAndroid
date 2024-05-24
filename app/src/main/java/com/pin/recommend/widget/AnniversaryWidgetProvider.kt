package com.pin.recommend.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.pin.recommend.MainActivity
import com.pin.recommend.R


/**
 * Implementation of App Widget functionality.
 */


class AnniversaryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        widgetManager.notifyAppWidgetViewDataChanged(
            widgetManager.getAppWidgetIds(
                ComponentName(
                    context.applicationContext.packageName,
                    AnniversaryWidgetProvider::class.java.name
                )
            ),
            R.id.anniversary_list
        )
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
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
        println("test!!onReceive ${intent.getStringExtra(ACTION_ITEM_CLICK)}")
        println("test!!onReceive ${intent.action}")
        when (intent.action) {
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
                updateAppWidget(context, widgetManager, appWidgetId)
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
            val a = db.get(appWidgetId)
            if (a != null) {
                val views = RemoteViews(context.packageName, R.layout.widget_display_anniversary)
                views.setImageViewBitmap(
                    R.id.background_image,
                    a.getIconImage(context, 200, 200)
                )
                a?.getBackgroundColor()?.let {
                    views.setInt(R.id.background_color, "setBackgroundColor", it)
                }
                views.setTextViewText(R.id.character_name, a?.getCharacterName() ?: "")
                views.setTextViewText(
                    R.id.remaining_days,
                    a?.getMessage() ?: ""
                )
                val pinDrawable = context.getDrawable(
                    R.drawable.pin_fill
                )
                a?.getTextColor()?.let {
                    views.setTextColor(R.id.anniversary_name, it)
                    views.setTextColor(R.id.remaining_days, it)
                    pinDrawable?.setTint(it)
                }

                val textBackground = context.getDrawable(R.drawable.shape_rounded_corners_10dp)
                a?.getTextShadowColor()?.let {
                    textBackground?.setTint(it)
                    views?.setImageViewBitmap(
                        R.id.character_name_background,
                        textBackground?.toBitmap(50, 50)
                    )
                    views?.setImageViewBitmap(
                        R.id.anniversary_background,
                        textBackground?.toBitmap(50, 50)
                    )
                    views?.setImageViewBitmap(
                        R.id.unpinning_background,
                        textBackground?.toBitmap(50, 50)
                    )
                    views?.setImageViewBitmap(R.id.unpinning, textBackground?.toBitmap(30, 30))
                }

                views.setImageViewBitmap(R.id.unpinning, pinDrawable?.toBitmap(30, 30))

                val intent = Intent(context, AnniversaryWidgetProvider::class.java)
                intent.action = ACTION_UNPINNING
                intent.putExtra(ACTION_UNPINNING, appWidgetId)
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

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } else {
                val serviceIntent = Intent(context, AnniversaryRemoteViewsService::class.java)
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
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

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

}

