package com.zwsi.gb.feature

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.util.AttributeSet
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBController

class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    private var strokeWidth: Int = 0
    private val sCenter = PointF()
    private val vCenter = PointF()
    private val paint = Paint()
    val bs = BitmapFactory.decodeResource(getResources(), R.drawable.star)

    init {
        initialise()
    }

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }

        paint.isAntiAlias = true
        paint.style = Style.STROKE
        paint.strokeCap = Cap.ROUND

        // Draw Stars and circles
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.argb(128, 128, 100, 22)
        val radius = scale * 500
        val offset = scale *22 // TODO need to calculate this so the star icon stays in the middle of the circle

        val stars = GBController.universe.allStars
        for (s in stars) {
            sCenter.set(s.x * 18f, s.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawCircle(vCenter.x, vCenter.y, radius, paint)
            sCenter.set(s.x * 18f, s.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawBitmap(bs, vCenter.x, vCenter.y, null)
        }

        // Draw Stars Names
        paint.textSize = 40f
        paint.style = Style.FILL
        paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
        for (s in stars) {
            sCenter.set(s.x * 18f + 30, s.y * 18f -10)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawText(s.name, vCenter.x + 30, vCenter.y - 10, paint)
        }


    }

}