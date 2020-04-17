package com.example.apps.pixo.model

import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaAlbum(
    var id: String,
    var name: String,
    var mediaId: Long,
    var mediaType: MediaType,
    var imageCount: Int,
    var createdAt: Long,
    var bitmap: Bitmap? = null
) : Parcelable {

    fun getContentUri(): Uri {
        return when (mediaType) {
            MediaType.Image -> {
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mediaId
                )
            }
            else -> {
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaId
                )
            }
        }
    }
}