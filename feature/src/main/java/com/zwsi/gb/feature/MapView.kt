package com.zwsi.gb.feature

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.util.AttributeSet
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBController
import kotlin.math.abs
import kotlin.math.min


class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    private var strokeWidth: Int = 0
    private val sCenter = PointF()
    private val vCenter = PointF()
    private val sCorner = PointF()
    private val vCorner = PointF()
    private val paint = Paint()
    private val debug = true
    private var bmStar: Bitmap? = null
    private var bmPlanet: Bitmap? = null

    init {
        initialise()
    }

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

        bmStar = BitmapFactory.decodeResource(getResources(), R.drawable.star)!!
        var w = density / 420f * bmStar!!.getWidth()
        var h = density / 420f * bmStar!!.getHeight()
        bmStar = Bitmap.createScaledBitmap(bmStar!!, w.toInt(), h.toInt(), true)!!

        bmPlanet = BitmapFactory.decodeResource(getResources(), R.drawable.planet)!!
        w = density / 420f * bmPlanet!!.getWidth()
        h = density / 420f * bmPlanet!!.getHeight()
        bmPlanet = Bitmap.createScaledBitmap(bmPlanet!!, w.toInt(), h.toInt(), true)!!
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }

        if (debug) { // State
            paint.textSize = 40f
            var stateLine = 1f
            val stateSkip = 50
            paint.style = Style.FILL
            paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
            canvas.drawText("maxScale: " + maxScale + " / minScale: " + minScale, 8f, stateLine++ * stateSkip, paint)
            canvas.drawText("Center: " + center!!.x.toInt()+", "+center!!.y.toInt(), 8f, stateLine++ * stateSkip, paint)
            val fRect = Rect()
            visibleFileRect(fRect)
            canvas.drawText(
                "Visible: " + (fRect.right - fRect.left) + " x " + (fRect.bottom - fRect.top) + " at " + fRect,
                8f,
                stateLine++ * stateSkip,
                paint
            )
            canvas.drawText("Scale: " + scale, 8f, stateLine++ * stateSkip, paint)
            canvas.drawText("Alpha1: " + (0.1-scale) * 10 * 255 , 8f, stateLine++ * stateSkip, paint)
            canvas.drawText("Alpha2: " + (0.1 - min(abs(0.6-scale), 0.6)) * 10 * 255 , 8f, stateLine++ * stateSkip, paint)
        }

        if (scale < 0.1) { // Draw universe grid lines at 250 Universe Coordinates
            val alpha = 128
            paint.color = Color.argb(alpha.toInt(), 100, 50, 0)
            for (x in 0 until 5) {
                sCenter.set(0f, x * 3600f)
                sCorner.set(18000f, x * 3600f)
                sourceToViewCoord(sCenter, vCenter)
                sourceToViewCoord(sCorner, vCorner)
                canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)
                sCenter.set(x * 3600f, 0f)
                sCorner.set(x * 3600f, 18000f)
                sourceToViewCoord(sCenter, vCenter)
                sourceToViewCoord(sCorner, vCorner)
                canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)
            }
        }

        if (scale > 0.4) { // Draw image grid lines at 1000 coordinates
            val alpha = 128
            paint.color = Color.argb(alpha.toInt(), 100, 100, 100)
            for (x in 0 until 18) {
                sCenter.set(0f, x * 1000f)
                sCorner.set(18000f, x * 1000f)
                sourceToViewCoord(sCenter, vCenter)
                sourceToViewCoord(sCorner, vCorner)
                canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)
                sCenter.set(x * 1000f, 0f)
                sCorner.set(x * 1000f, 18000f)
                sourceToViewCoord(sCenter, vCenter)
                sourceToViewCoord(sCorner, vCorner)
                canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)
            }
        }

        if (scale > 1.1) { // Draw Planets
            val stars = GBController.universe.allStars
            for (s in stars) {
                sCenter.set((s.x) * 18f + 100, (s.y) * 18f+100)
                sourceToViewCoord(sCenter, vCenter)
                canvas.drawBitmap(bmPlanet!!, vCenter.x - bmPlanet!!.getWidth()/2, vCenter.y - bmPlanet!!.getWidth()/2, null)
            }
        }


        paint.isAntiAlias = true
        paint.style = Style.STROKE
        paint.strokeCap = Cap.ROUND

        // Draw Stars and circles
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.argb(128, 128, 100, 22)
        val radius = scale * 250f

        val stars = GBController.universe.allStars
        for (s in stars) {
            sCenter.set(s.x * 18f, s.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawCircle(vCenter.x, vCenter.y, radius, paint)
            sCenter.set(s.x * 18f, s.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawBitmap(bmStar!!, vCenter.x - bmStar!!.getWidth()/2, vCenter.y - bmStar!!.getWidth()/2, null)
        }


        // Draw Stars Names
        paint.textSize = 40f
        paint.style = Style.FILL
        paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
        for (s in stars) {
            sCenter.set(s.x * 18f + 30, s.y * 18f - 10)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawText(s.name, vCenter.x + 30, vCenter.y - 10, paint)
        }


    }

}