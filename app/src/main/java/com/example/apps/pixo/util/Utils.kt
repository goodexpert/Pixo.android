package com.example.apps.pixo.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.example.apps.pixo.Const
import io.reactivex.rxjava3.core.Single
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@Throws(IOException::class)
fun createImageFile(parentDir: File?): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val filename: String = "IMG_${timestamp}.jpg"

    val mediaDir = File(parentDir, "images")
    if (!mediaDir.exists()) {
        if (!mediaDir.mkdir()) {
            throw IOException("Could not make a directory.")
        }
    }

    return File(mediaDir, filename)
}

fun saveMediaFile(context: Context, file: File) {
    // Add a media item that other apps shouldn't see until the item is
    // fully written to the media store.
    val resolver = context.contentResolver

    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Images.Media.DESCRIPTION, file.name)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        } else {
            put("_data", file.absolutePath)
        }
    }

    val contentUri = resolver.insert(collection, imageDetails)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentUri?.let {
            resolver.openFileDescriptor(contentUri, "w", null)?.let { pfd ->
                // Write data into the pending audio file.
                val fin = FileInputStream(file)
                val fos = FileOutputStream(pfd.fileDescriptor)

                val buffer = ByteArray(1024)
                var len = fin.read(buffer)

                while (len > 0) {
                    fos.write(buffer, 0, len)
                    len = fin.read(buffer)
                }

                fin.close()
                fos.flush()
                fos.close()
            }
        }
    }

    imageDetails.clear()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)

        contentUri?.let {
            resolver.update(contentUri, imageDetails, null, null)
        }
    }
}

fun View.takeScreenShot(): Single<File> {
    return Single.create {
        try {
            // Take a screen shot
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)

            // Create an image file name
            val file = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    createImageFile(context.externalMediaDirs[0])
                }
                else -> {
                    createImageFile(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                }
            }.apply {
                val fos = FileOutputStream(this)

                bitmap.compress(Bitmap.CompressFormat.JPEG, Const.COMPRESS_JPEG_QUALITY, fos)
                fos.flush()
                fos.close()
            }
            it.onSuccess(file)
        } catch (e: Throwable) {
            it.onError(e)
        }
    }
}

fun Uri.getMediaSize(context: Context): Point {
    val columns = arrayOf(MediaStore.Video.Media.WIDTH, MediaStore.Video.Media.HEIGHT)
    val cursor = context.contentResolver.query(this, columns, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            val wIndex = cursor.getColumnIndex(columns[0])
            val hIndex = cursor.getColumnIndex(columns[0])
            val w = cursor.getInt(wIndex)
            val h = cursor.getInt(hIndex)

            cursor.close()
            return Point(w, h)
        }
        cursor.close()
    }
    return Point(0, 0)
}

@Suppress("DEPRECATION")
fun Uri.toFilePath(context: Context): String {
    val columns = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(this, columns, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(columns[0])
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return filePath
        }
        cursor.close()
    }
    throw FileNotFoundException(this.path)
}