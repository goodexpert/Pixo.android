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
import com.example.apps.pixo.model.MediaAlbum
import com.example.apps.pixo.model.MediaType

class AlbumViewModel(application: Application) : AndroidViewModel(application) {

    private val contentResolver: ContentResolver by lazy { application.contentResolver }
    private val contentObserver: ContentObserver

    val albumsLiveData: MutableLiveData<List<MediaAlbum>> by lazy { MutableLiveData<List<MediaAlbum>>() }

    init {
        this.contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                updateData()
            }
        }

        this.contentResolver.registerContentObserver(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), true, this.contentObserver)
        this.contentResolver.registerContentObserver(
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), true, this.contentObserver)

        updateData()
    }

    override fun onCleared() {
        this.contentResolver.unregisterContentObserver(this.contentObserver)
        this.albumsLiveData.value = null
        super.onCleared()
    }

    private fun getMediaCursor(uri: Uri): Cursor? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        // Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        return ContentResolverCompat.query(
            contentResolver,
            uri,
            projection,
            null,
            null,
            sortOrder,
            null
        )
    }

    private fun update(cursor: Cursor?, list: MutableList<MediaAlbum>, mediaType: MediaType) {
        cursor?.let {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val mediaIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(idColumn)
                    val name = cursor.getString(nameColumn)
                    val mediaId = cursor.getLong(mediaIdColumn)
                    val createdAt = cursor.getLong(dateAddedColumn)

                    val found = list.find { id == it.id }
                    if (found != null) {
                        if (found.createdAt < createdAt) {
                            found.mediaId = mediaId
                            found.mediaType = mediaType
                            found.createdAt = createdAt
                        }
                        found.imageCount++
                    } else {
                        list += MediaAlbum(id, name, mediaId, mediaType, 1, createdAt)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    fun updateData() {
        val albumList = mutableListOf<MediaAlbum>()

        val imageCursor = getMediaCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        update(imageCursor, albumList, MediaType.Image)

        val videoCursor = getMediaCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        update(videoCursor, albumList, MediaType.Video)

        albumList.sortBy { it.name }
        this.albumsLiveData.postValue(albumList)
    }
}