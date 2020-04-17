package com.example.apps.pixo.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.apps.pixo.model.MediaFile
import com.example.apps.pixo.model.MediaType

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val contentResolver: ContentResolver by lazy { application.contentResolver }
    private val contentObserver: ContentObserver

    private var bucketId: String? = null

    val mediaFilesLiveData: MutableLiveData<List<MediaFile>> by lazy { MutableLiveData<List<MediaFile>>() }

    init {
        this.contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
            }
        }

        this.contentResolver.registerContentObserver(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), true, this.contentObserver)
        this.contentResolver.registerContentObserver(
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), true, this.contentObserver)
    }

    override fun onCleared() {
        this.contentResolver.unregisterContentObserver(this.contentObserver)
        mediaFilesLiveData.value = null
        super.onCleared()
    }

    private fun getMediaCursor(uri: Uri, bucketId: String): Cursor? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DESCRIPTION,
            MediaStore.Images.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        // Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} ASC"

        return ContentResolverCompat.query(
            contentResolver,
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder,
            null
        )
    }

    private fun update(cursor: Cursor?, list: MutableList<MediaFile>, mediaType: MediaType) {
        cursor?.let {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val descColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val description = cursor.getString(descColumn)
                    val createdAt = cursor.getLong(dateAddedColumn)

                    list += MediaFile(id, name, description, mediaType, createdAt)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    fun setFilter(bucketId: String) {
        this.bucketId = bucketId
        updateData()
    }

    fun updateData() {
        bucketId?.let {
            val mediaList = mutableListOf<MediaFile>()

            val imageCursor = getMediaCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it)
            update(imageCursor, mediaList, MediaType.Image)

            val videoCursor = getMediaCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, it)
            update(videoCursor, mediaList, MediaType.Video)

            mediaList.sortByDescending { it.createdAt }
            this.mediaFilesLiveData.postValue(mediaList)
        }
    }
}