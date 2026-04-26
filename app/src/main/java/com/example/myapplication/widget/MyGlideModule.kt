package com.example.myapplication.widget

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.myapplication.ha.HomeAssistantClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

@GlideModule
class MyGlideModule : AppGlideModule() {
    override fun registerComponents(context: android.content.Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val request: Request = chain.request()
                // Добавляем токен только для запросов к нашему серверу
                val newRequest = if (request.url.toString().startsWith(HomeAssistantClient.BASE_URL)) {
                    request.newBuilder()
                        .addHeader("Authorization", HomeAssistantClient.TOKEN)
                        .build()
                } else {
                    request
                }
                chain.proceed(newRequest)
            }
            .build()

        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }
}
