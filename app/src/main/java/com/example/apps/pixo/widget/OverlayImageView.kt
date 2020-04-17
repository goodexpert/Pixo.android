package com.example.apps.pixo.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

class OverlayImageView : AppCompatImageView {

    private val overlaySize: Int by lazy { (resources.displayMetrics.density * 240).toInt() }
    private val overlayPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = resources.displayMetrics.density * 1
        }
    }

    private var overlayBitmap: Bitmap? = null
    private var overlayWidth: Int = 0
    private var overlayHeight: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        overlayBitmap?.let { bitmap ->
            val left = (width - overlaySize) / 2
            val top = (height - overlaySize) / 2
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            val dest = Rect(left, top, left + overlaySize, top + overlaySize)

            canvas.drawBitmap(bitmap, src, dest, overlayPaint)
        }
    }

    fun setOverlayBitmap(bitmap: Bitmap) {
        overlayBitmap = bitmap
        overlayWidth = bitmap.width
        overlayHeight = bitmap.height
        invalidate()
    }

    fun setOverlayDrawable(drawable: Drawable) {
        overlayBitmap = drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        invalidate()
    }

    fun setOverlayResource(resId: Int) {
        ContextCompat.getDrawable(context, resId)?.let { drawable ->
            overlayBitmap = drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            invalidate()
        }
    }
}