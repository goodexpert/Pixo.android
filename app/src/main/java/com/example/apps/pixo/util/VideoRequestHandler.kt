package com.example.apps.pixo.util

import android.app.Application
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler

class VideoRequestHandler(private val application: Application) : RequestHandler() {

    override fun canHandleRequest(data: Request): Boolean {
        return "content".equals(data.uri.scheme, true) and data.uri.pathSegments.contains("video")
    }

    @Suppress("DEPRECATION")
    override fun load(request: Request, networkPolicy: Int): Result? {
        val bitmap = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val size: Size = when {
                    request.hasSize() -> Size(request.targetWidth, request.targetHeight)
                    else -> {
                        val point = request.uri.getMediaSize(application)
                        Size(point.x, point.y)
                    }
                }
                application.contentResolver.loadThumbnail(request.uri, size, null)
            }
            else -> {
                val kind: Int = when {
                    request.hasSize() -> MediaStore.Video.Thumbnails.MINI_KIND
                    else -> MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                }
                ThumbnailUtils.createVideoThumbnail(request.uri.toFilePath(application), kind)
            }
        }
        return Result(bitmap, Picasso.LoadedFrom.DISK)
    }
}