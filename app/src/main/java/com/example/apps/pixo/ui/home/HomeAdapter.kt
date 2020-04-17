package com.example.apps.pixo.ui.home

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.apps.pixo.R
import com.example.apps.pixo.model.MediaAlbum
import com.example.apps.pixo.ui.base.BaseAdapter
import com.example.apps.pixo.ui.base.BaseViewHolder
import com.squareup.picasso.Picasso

class HomeAdapter(activity: Activity) : BaseAdapter<MediaAlbum>(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MediaAlbum> {
        val view = layoutInflater().inflate(R.layout.view_album_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder : BaseViewHolder<MediaAlbum> {
        private val image: ImageView
        private val title: TextView
        private val description: TextView

        constructor(itemView: View) : super(itemView) {
            this.image = itemView.findViewById(R.id.image)
            this.title = itemView.findViewById(R.id.title)
            this.description = itemView.findViewById(R.id.description)
        }

        override fun onBindViewHolder(position: Int, data: MediaAlbum, listener: OnItemClickedListener?) {
            this.title.text = data.name
            this.description.text = "${data.imageCount} images"
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