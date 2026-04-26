package com.example.myapplication.widget

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ha.EntityState
import com.example.myapplication.ha.HomeAssistantClient
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class MediaPlayerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Получаем состояние плеера
        val state = try {
            HomeAssistantClient.service.getEntityState(
                HomeAssistantClient.TOKEN,
                HomeAssistantClient.ENTITY_ID
            )
        } catch (e: Exception) {
            null
        }

        // Скачиваем обложку в кэш и преобразуем в Bitmap
        val bitmap = state?.attributes?.entityPicture
            ?.takeIf { it.isNotBlank() }
            ?.let { url -> downloadToCache(context, url) }
            ?.let { file -> BitmapFactory.decodeFile(file.absolutePath) }
        val imageProv = bitmap?.let { ImageProvider(it) }

        provideContent {
            WidgetContent(state, imageProv)
        }
    }

    private fun downloadToCache(context: Context, url: String): File? {
        return try {
            val client = OkHttpClient()
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            if (!response.isSuccessful) return null
            val file = File(context.cacheDir, "ha_cover.jpg")
            response.body?.byteStream()?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    private fun WidgetContent(state: EntityState?, imageProv: ImageProvider?) {
        val active = state != null && imageProv != null
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.Black))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!active) {
                // Плейсхолдер
                Text(
                    text = "PS5",
                    style = TextStyle(color = ColorProvider(Color.White), fontSize = 24.sp)
                )
            } else {
                // Отображаем обложку
                Image(
                    provider = imageProv,
                    contentDescription = null,
                    modifier = GlanceModifier.size(64.dp).padding(bottom = 4.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state!!.attributes.mediaTitle ?: "No Title",
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 16.sp)
                    )
                    Text(
                        text = state.attributes.friendlyName ?: "PlayStation 5",
                        style = TextStyle(color = ColorProvider(Color.LightGray), fontSize = 14.sp)
                    )
                }
            }
        }
    }
}