package com.example.apps.pixo.ui.list

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.apps.pixo.R
import com.example.apps.pixo.model.MediaFile
import com.example.apps.pixo.ui.base.BaseAdapter
import com.example.apps.pixo.ui.base.BaseViewHolder
import com.squareup.picasso.Picasso

class ListAdapter(activity: Activity) : BaseAdapter<MediaFile>(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MediaFile> {
        val view = layoutInflater().inflate(R.layout.view_media_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder : BaseViewHolder<MediaFile> {
        private val image: ImageView

        constructor(itemView: View) : super(itemView) {
            this.image = itemView.findViewById(R.id.image)
        }

        override fun onBindViewHolder(position: Int, data: MediaFile, listener: OnItemClickedListener?) {
            this.itemView.setOnClickListener {
                listener?.onItemClicked(position, data)
            }

            if (data.bitmap != null) {
                image.setImageBitmap(data.bitmap)
            } else {
                Picasso.get()
                    .load(data.getContentUri())
                    .fit()
                    .into(image)
            }
        }
    }
}