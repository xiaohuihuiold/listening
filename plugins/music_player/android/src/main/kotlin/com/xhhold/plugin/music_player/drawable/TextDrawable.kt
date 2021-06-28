package com.xhhold.plugin.music_player.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import com.xhhold.plugin.music_player.ext.generateMd5

class TextDrawable(private val text: String?) : Drawable() {

    companion object {
        val COLORS = intArrayOf(0xFFFF9100.toInt(), 0xFFFF1744.toInt(), 0xFF2196F3.toInt())
    }

    private val backgroundColor =
        if (text == null) null else COLORS[(text.generateMd5().toCharArray()[12]).toInt() % 3]


    private val paint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.WHITE
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun draw(canvas: Canvas) {
        if (backgroundColor != null) {
            canvas.drawColor(backgroundColor)
        }
        val centerX = bounds.width() / 2.0f
        val centerY = bounds.height() / 2.0f
        text?.apply {
            canvas.save()
            paint.textSize = bounds.width().toFloat() * 2
            var i = 0
            for (str in take(4)) {
                i++
                canvas.rotate(i * 45.0f, centerX, centerY)
                paint.alpha = 75
                canvas.drawText(str.toString(), centerX, centerY, paint)
            }
            canvas.restore()
        }
    }

    fun toBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            if (opacity == PixelFormat.OPAQUE) Bitmap.Config.RGB_565 else Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        setBounds(0, 0, 512, 512)
        draw(canvas)
        return bitmap
    }
}