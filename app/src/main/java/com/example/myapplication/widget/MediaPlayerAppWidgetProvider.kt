package com.example.myapplication.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.example.myapplication.R
import com.example.myapplication.ha.HomeAssistantClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Classic AppWidgetProvider to display Home Assistant media player cover and title.
 */
class MediaPlayerAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Launch background work to update widgets
        CoroutineScope(Dispatchers.IO).launch {
            updateAllWidgets(context)
        }
    }

    override fun onReceive(context: Context, intent: android.content.Intent) {
        super.onReceive(context, intent)
        // Also update on widget enabled or custom intent if needed
    }

    private suspend fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, MediaPlayerAppWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        widgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Fetch state from Home Assistant
        val state = try {
            HomeAssistantClient.service.getEntityState(
                HomeAssistantClient.TOKEN,
                HomeAssistantClient.ENTITY_ID
            )
        } catch (e: Exception) {
            null
        }

        // Prepare RemoteViews
        val views = RemoteViews(context.packageName, R.layout.widget_media_player)

        // Set title and subtext
        val title = state?.attributes?.mediaTitle ?: "PS5"
        val subtitle = state?.attributes?.friendlyName ?: "PlayStation 5"
        views.setTextViewText(R.id.title, title)
        views.setTextViewText(R.id.subtext, subtitle)

        // Load and set cover image using Glide
        val relativePictureUrl = state?.attributes?.entityPictureLocal ?: state?.attributes?.entityPicture
        if (!relativePictureUrl.isNullOrBlank()) {
            val fullUrl = if (relativePictureUrl.startsWith("http")) {
                relativePictureUrl
            } else {
                HomeAssistantClient.BASE_URL.removeSuffix("/") + relativePictureUrl
            }

            val appWidgetTarget = com.bumptech.glide.request.target.AppWidgetTarget(
                context.applicationContext,
                R.id.cover,
                views,
                appWidgetId
            )

            com.bumptech.glide.Glide.with(context.applicationContext)
                .asBitmap()
                .load(fullUrl)
                .into(appWidgetTarget)
        }

        // Update the widget
        // UI update must be on main thread
        Handler(Looper.getMainLooper()).post {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
