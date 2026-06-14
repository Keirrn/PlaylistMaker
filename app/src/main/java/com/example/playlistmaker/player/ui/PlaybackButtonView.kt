package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private var isPlaying: Boolean = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playBitmap = getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap()
                pauseBitmap = getDrawable( R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentBitmap = if (isPlaying) pauseBitmap else playBitmap

        currentBitmap?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPlayingState(isPlayingNow: Boolean) {
        if (this.isPlaying != isPlayingNow) {
            this.isPlaying = isPlayingNow
            invalidate()
        }
    }
}