package com.example.myapplication.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.GlanceTheme
import com.example.myapplication.ha.EntityState
import com.example.myapplication.ha.HomeAssistantClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaPlayerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = try {
            withContext(Dispatchers.IO) {
                HomeAssistantClient.service.getEntityState(
                    HomeAssistantClient.TOKEN,
                    HomeAssistantClient.ENTITY_ID
                )
            }
        } catch (e: Exception) {
            null
        }

        provideContent {
            WidgetContent(state)
        }
    }

    @Composable
    private fun WidgetContent(state: EntityState?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            if (state == null) {
                Text(
                    text = "Error loading data",
                    style = TextStyle(color = ColorProvider(Color.White))
                )
            } else {
                Text(
                    text = state.attributes.mediaTitle ?: "No Title",
                    style = TextStyle(color = ColorProvider(Color.White))
                )
                Text(
                    text = state.attributes.mediaArtist ?: "Unknown Artist",
                    style = TextStyle(color = ColorProvider(Color.LightGray))
                )
                Text(
                    text = "Status: ${state.state}",
                    style = TextStyle(color = ColorProvider(Color.Cyan))
                )
            }
        }
    }
}
