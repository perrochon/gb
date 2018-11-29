package com.zwsi.gb.feature

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.Rect.intersects
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBController
import kotlin.math.cos
import kotlin.math.sin


class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr), View.OnTouchListener {

    private var strokeWidth: Int = 0
    private var density = 0f
    private val sCenter = PointF()
    private val vCenter = PointF()
    private val sCorner = PointF()
    private val vCorner = PointF()
    private val paint = Paint()
    private val debug = true
    private var bmStar: Bitmap? = null
    private var bmPlanet: Bitmap? = null
    private var bmRace: Bitmap? = null
    private var normScale: Float = 0f
    val visibleRect = Rect()

    private var sClick = PointF()

    private var xClick = 0f
    private var yClick = 0f

    val stars = GBController.universe.allStars

    init {
        initialise()

    }

    private fun initialise() {
        density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

        bmStar = BitmapFactory.decodeResource(getResources(), R.drawable.star)!!
        var w = density / 420f * bmStar!!.getWidth()
        var h = density / 420f * bmStar!!.getHeight()
        bmStar = Bitmap.createScaledBitmap(bmStar!!, w.toInt(), h.toInt(), true)!!

        bmPlanet = BitmapFactory.decodeResource(getResources(), R.drawable.planet)!!
        w = density / 420f * bmPlanet!!.getWidth() / 2
        h = density / 840f * bmPlanet!!.getHeight() / 2
        bmPlanet = Bitmap.createScaledBitmap(bmPlanet!!, w.toInt(), h.toInt(), true)!!

        bmRace = BitmapFactory.decodeResource(getResources(), R.drawable.xenost)!!
        w = density / 420f * bmRace!!.getWidth() / 30
        h = density / 420f * bmRace!!.getHeight() / 30
        bmRace = Bitmap.createScaledBitmap(bmRace!!, w.toInt(), h.toInt(), true)!!

        setOnTouchListener(this);

        maxScale = 12f
        setDebug(false)

    }

    override fun onDraw(canvas: Canvas) {

        if (scale < 5)
            super.onDraw(canvas)



        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }

        normScale = ((1 / scale) - (1 / maxScale)) / (1 / minScale - 1 / maxScale) * 100



        visibleFileRect(visibleRect)

        if (debug) { // State
            paint.textSize = 40f
            var stateLine = 1f
            val stateSkip = 50
            paint.style = Style.FILL
            paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
            canvas.drawText(
                "maxScale: " + maxScale + " / minScale: " + minScale + " / density: " + density,
                8f,
                stateLine++ * stateSkip,
                paint
            )
            canvas.drawText("Normscale: " + normScale + " Scale: " + scale, 8f, stateLine++ * stateSkip, paint)
            canvas.drawText(
                "UCenter: " + center!!.x.toInt() / 18 + ", " + center!!.y.toInt() / 18 +
                        " SCenter: " + center!!.x.toInt() + ", " + center!!.y.toInt(),
                8f,
                stateLine++ * stateSkip,
                paint
            )
            canvas.drawText(
                "U Visible: " + (visibleRect.right - visibleRect.left) / 18 + " x " + (visibleRect.bottom - visibleRect.top) / 18,
                8f,
                stateLine++ * stateSkip,
                paint
            )
            canvas.drawText(
                "SVisible: " + (visibleRect.right - visibleRect.left) + " x " + (visibleRect.bottom - visibleRect.top) + " at " + visibleRect,
                8f,
                stateLine++ * stateSkip,
                paint
            )
            canvas.drawText("Screen Click: (" + xClick + ", " + yClick + ")", 8f, stateLine++ * stateSkip, paint)
            canvas.drawText("Source Click: (" + sClick.x + ", " + sClick.y + ")", 8f, stateLine++ * stateSkip, paint)
            canvas.drawText(
                "Universe Click: (" + sClick.x / 18 + ", " + sClick.y / 18 + ")",
                8f,
                stateLine++ * stateSkip,
                paint
            )


        }


        // Always draw stars
        paint.isAntiAlias = true
        paint.style = Style.STROKE
        paint.strokeCap = Cap.ROUND
        for (s in stars) {
            sCenter.set(s.loc.x * 18f, s.loc.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawBitmap(bmStar!!, vCenter.x - bmStar!!.getWidth() / 2, vCenter.y - bmStar!!.getWidth() / 2, null)
        }

        // Always draw Stars Names
        paint.textSize = 40f
        paint.style = Style.FILL
        paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
        for (s in stars) {
            sCenter.set(s.loc.x * 18f + 30, s.loc.y * 18f - 10)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawText(s.name, vCenter.x + 30, vCenter.y - 10, paint)
        }


        if (normScale > 99) { // Draw universe grid lines at 250 Universe Coordinates
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

        if ((70 > normScale) && (normScale > 70)) { // Draw image grid lines at 1000 coordinates
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

        if (40 > normScale) { // Draw circles
            paint.style = Style.STROKE
            paint.strokeWidth = strokeWidth.toFloat()
            paint.color = Color.argb(65, 128, 100, 22)
            val radius = scale * 250f

            for (s in stars) {
                sCenter.set(s.loc.x * 18f, s.loc.y * 18f)
                sourceToViewCoord(sCenter, vCenter)
                canvas.drawCircle(vCenter.x, vCenter.y, radius, paint)
            }
        }

        if (normScale > 20) { // draw races - remove once we show planets
            val s = GBController.universe.allStars[0]
            sCenter.set(s.loc.x * 18f + 50, s.loc.y * 18f)
            sourceToViewCoord(sCenter, vCenter)
            canvas.drawBitmap(bmRace!!, vCenter.x, vCenter.y, null)
        }

        if (50 > normScale){ // draw deep space ships

            for (sh in GBController.universe.universeShips){

                paint.style = Style.STROKE
                paint.strokeWidth = strokeWidth.toFloat()
                paint.color = Color.argb(255, 0, 255, 0)
                val radius = scale * 2f

                sCenter.set(sh.loc.getLoc().x*18, sh.loc.getLoc().y*18)
                sourceToViewCoord(sCenter, vCenter)

                canvas.drawCircle(vCenter.x, vCenter.y, radius, paint)

                paint.strokeWidth = strokeWidth.toFloat()/2
                paint.color = Color.argb(128, 0, 0, 255)

                var from = sh.loc.getLoc()

                val iterate = sh.trail.descendingIterator()

                while (iterate.hasNext()) {

                    val xy = iterate.next()

                    sCenter.set(from.x*18, from.y*18)
                    sourceToViewCoord(sCenter, vCenter)
                    sCorner.set(xy.x*18, xy.y*18)
                    sourceToViewCoord(sCorner, vCorner)

                    canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)

                    from = xy
                }
            }

        }

        if (10 > normScale) { // Draw Planets
            val stars = GBController.universe.allStars
            for (s in stars) {
                val starRect = Rect(s.loc.x.toInt() * 18 - 250, s.loc.y.toInt() * 18 - 250, s.loc.x.toInt() * 18 + 250, s.loc.y.toInt() * 18 + 250)
                if (intersects(starRect, visibleRect)) {

                    for (p in s.starPlanets) {

                        sCenter.set(p.loc.x*18, p.loc.y*18)
                        sourceToViewCoord(sCenter, vCenter)
                        canvas.drawBitmap(
                            bmPlanet!!,
                            vCenter.x - bmPlanet!!.getWidth() / 2,
                            vCenter.y - bmPlanet!!.getWidth() / 2,
                            null
                        )

                    } // planet loop

                    for (sh in s.starShips){

                        paint.style = Style.STROKE
                        paint.strokeWidth = strokeWidth.toFloat()
                        paint.color = Color.argb(255, 255, 0, 0)
                        val radius = scale * 2f

                        sCenter.set(sh.loc.getLoc().x*18, sh.loc.getLoc().y*18)
                        sourceToViewCoord(sCenter, vCenter)

                        canvas.drawCircle(vCenter.x, vCenter.y, radius, paint)

                        paint.strokeWidth = strokeWidth.toFloat()/2
                        paint.color = Color.argb(128, 0, 0, 255)

                        var from = sh.loc.getLoc()

                        val iterate = sh.trail.descendingIterator()

                        while (iterate.hasNext()) {

                            val xy = iterate.next()

                            sCenter.set(from.x*18, from.y*18)
                            sourceToViewCoord(sCenter, vCenter)
                            sCorner.set(xy.x*18, xy.y*18)
                            sourceToViewCoord(sCorner, vCorner)

                            canvas.drawLine(vCenter.x, vCenter.y, vCorner.x, vCorner.y, paint)

                            from = xy
                        }
                    }

                    } // ships loop
                }// if star visible?
            }// star loop
        }



    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        xClick = motionEvent.x
        yClick = motionEvent.y

        sClick = viewToSourceCoord(xClick, yClick)!!

        invalidate()

        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return super.onTouchEvent(event)

    }
}
