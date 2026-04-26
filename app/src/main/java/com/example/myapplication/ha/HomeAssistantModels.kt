package com.example.myapplication.ha

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntityState(
    @SerialName("entity_id") val entityId: String,
    val state: String,
    val attributes: MediaAttributes
)

@Serializable
data class MediaAttributes(
    @SerialName("media_title") val mediaTitle: String? = null,
    @SerialName("media_artist") val mediaArtist: String? = null,
    @SerialName("entity_picture") val entityPicture: String? = null,
    @SerialName("friendly_name") val friendlyName: String? = null
)
