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
import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBxy
import kotlin.system.measureNanoTime

class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr), View.OnTouchListener {

    // Fields initialized in init
    private var density = 0f
    private var strokeWidth: Int = 0

    private var normScale: Float = 1f // used to make decisions on what to draw at what level
    private val gbDebug = true // show debugbox

    private var bmStar: Bitmap? = null
    private var bmPlanet: Bitmap? = null
    private var bmRace: Bitmap? = null

    val sourceSize = 18000 // TODO get from elsewhere
    val universeSize = 1000
    val uToS = sourceSize / universeSize
    val uToSf = uToS.toFloat()
    val sSystemSize = 13 * uToS


    // Object creation outside onDraw. These are only used in onDraw, but here for performance reasons?
    private val paint = Paint()
    private val debugTextColor = Color.parseColor("#FFffbb33")
    private val labelColor = Color.parseColor("#FFffbb33")
    private val podColorSystem = Color.parseColor("#ffee1111")
    private val podColorDeepspace = Color.parseColor("#ff11ee11")
    private val trailColor = Color.parseColor("#40bbbbbb")
    private val gridColor = Color.parseColor("#10bbbbbb")
    private val circleColor = Color.parseColor("#20FF6015")

    val vr = Rect()
    val rect = Rect()

    private val sP1 = PointF()
    private val vP1 = PointF()
    private val sP2 = PointF()
    private val vP2 = PointF()

    private var sClick = PointF()
    private var xClick = 0f
    private var yClick = 0f

    var times = mutableMapOf<String, Long>()
    var startTimeNanos = 0L
    var drawUntilStats = 0L
    var last20 = arrayListOf<Long>(60)
    var numberOfDraws = 0L

    init {
        initialise()
    }

    private fun initialise() {

        density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

        paint.isAntiAlias = true
        paint.strokeCap = Cap.ROUND
        paint.strokeWidth = strokeWidth.toFloat()


        // Get bitmaps we'll use later.
        bmStar = BitmapFactory.decodeResource(getResources(), R.drawable.star)!!
        var w = density / 420f * bmStar!!.getWidth()
        var h = density / 420f * bmStar!!.getHeight()
        bmStar = Bitmap.createScaledBitmap(bmStar!!, w.toInt(), h.toInt(), true)!!

        bmPlanet = BitmapFactory.decodeResource(getResources(), R.drawable.planet)!!
        w = density / 420f * bmPlanet!!.getWidth() / 2
        h = density / 420f * bmPlanet!!.getHeight() / 2
        bmPlanet = Bitmap.createScaledBitmap(bmPlanet!!, w.toInt(), h.toInt(), true)!!

        bmRace = BitmapFactory.decodeResource(getResources(), R.drawable.xenost)!!
        w = density / 420f * bmRace!!.getWidth() / 30
        h = density / 420f * bmRace!!.getHeight() / 30
        bmRace = Bitmap.createScaledBitmap(bmRace!!, w.toInt(), h.toInt(), true)!!


        setOnTouchListener(this);

        // set behavior of parent
        setDebug(false)
        maxScale = 12f

    }

    override fun onDraw(canvas: Canvas) {


        // Don't show the tiles on high zoom, as it's blurry anyway
        if (scale < 8)
            super.onDraw(canvas)

        // Don't draw anything before image is ready
        if (!isReady) {
            return
        }

        startTimeNanos = System.nanoTime()
        numberOfDraws++

        GBViewModel.update()

        normScale = ((1 / scale) - (1 / maxScale)) / (1 / minScale - 1 / maxScale) * 100

        visibleFileRect(vr)

        times["Grids"] = measureNanoTime { drawGrids(canvas) }

        times["Stars and Circles"] = measureNanoTime { drawStarsAndCircles(canvas) }

        times["Planets"] = measureNanoTime { drawPlanets(canvas) }

        times["DeepSpaceShips"] = measureNanoTime { drawDeepSpaceShips(canvas) }

        times["Star Names"] = measureNanoTime { drawStarNames(canvas) }

        times["Races"] = measureNanoTime { drawRaces(canvas) }


        drawUntilStats = System.nanoTime() - startTimeNanos
        last20[(numberOfDraws % last20.size).toInt()] = drawUntilStats

        drawStats(canvas)

        this.postDelayed({ this.invalidate() }, 1000) //TODO A better way to refresh upon model changes


    } // onDraw

    private fun drawStats(canvas: Canvas) {
        if (gbDebug) {

            paint.textSize = 40f
            paint.style = Style.FILL
            paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
            paint.color = debugTextColor
            paint.alpha = 255

            var l = 1f
            val h = 50

            //            canvas.drawText("maxScale: $maxScale / minScale: $minScale / density: $density", 8f, l++ * h, paint)
            canvas.drawText("Normscale: $normScale/ Scale: $scale", 8f, l++ * h, paint)
            //            canvas.drawText(
            //                "UCenter: ${center!!.x.toInt() / uToS}, ${center!!.y.toInt() / uToS} / "
            //                        + "SCenter: ${center!!.x.toInt()}, ${center!!.y.toInt()}", 8f, l++ * h, paint
            //            )
            //            canvas.drawText(
            //                "Uvisible: ${(vr.right - vr.left) / uToS}x${(vr.bottom - vr.top) / uToS}",
            //                8f,
            //                l++ * h,
            //                paint
            //            )
            //            canvas.drawText(
            //                "Svisible: ${(vr.right - vr.left)}x${(vr.bottom - vr.top)}" + " at " + vr,
            //                8f,
            //                l++ * h,
            //                paint
            //            )
            //            canvas.drawText("Screen Click: ($xClick, $yClick)", 8f, l++ * h, paint)
            //            canvas.drawText("Source Click: (${sClick.x},${sClick.y})", 8f, l++ * h, paint)
            //            canvas.drawText(
            //                "Universe Click: (${sClick.x / uToS},${sClick.y / uToS})", 8f, l++ * h, paint
            //            )
            canvas.drawText(
                "Ships: ${GBViewModel.viewShips.size} | UShips: ${GBViewModel.viewUniverseShips}",
                8f,
                l++ * h,
                paint
            )
            canvas.drawText(
                "Turn: ${universe.turn} / Update time: ${GBViewModel.updateTimeTurn / 1000}μs",
                8f,
                l++ * h,
                paint
            )
            times.forEach { t, u -> canvas.drawText("$t: ${u / 100L}μs", 8f, l++ * h, paint) }

            canvas.drawText(
                "Draw Time: ${(last20.average()!!/1000).toInt()}μs",
                8f,
                l++ * h,
                paint
            )
        }
    }

    private fun drawRaces(canvas: Canvas) {
        // Timing Info:  no race 200μs, 1 race 400μs, more ?μs
        if (normScale > 50) {

            val s = GBViewModel.viewStars.get(0)
            if (visible(s.loc.x.toInt() * uToS, s.loc.y.toInt() * uToS)) {
                sP1.set(s.loc.getLoc().x * uToSf + 50, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawBitmap(bmRace!!, vP1.x, vP1.y, null)
            }
        }
    }

    private fun drawStarNames(canvas: Canvas) {
        // Timing Info:  no star 500μs, 1 star 600μs, 15 stars 900μs
        paint.textSize = 50f
        paint.style = Style.FILL
        paint.color = labelColor
        paint.alpha = 128
        for (s in GBViewModel.viewStars) {
            if (visible(s.loc.getLoc().x.toInt() * uToS, s.loc.getLoc().y.toInt() * uToS)) {
                sP1.set(s.loc.getLoc().x * uToSf + 30, s.loc.getLoc().y * uToSf - 10)
                sourceToViewCoord(sP1, vP1)
                canvas.drawText(s.name, vP1.x + 30, vP1.y - 10, paint)
            }
        }
    }

    private fun drawDeepSpaceShips(canvas: Canvas) {
        // Timing Info:  no ships 300μs, 50 ships  2000μs, 500 ships 900μs (at beginning)
        if (101 >= normScale) {
            for (sh in GBViewModel.viewUniverseShips) {
                if (visible(sh.loc.getLoc().x.toInt() * uToS, sh.loc.getLoc().y.toInt()* uToS)) { // TODO why this not workie
                drawShip(canvas, sh, podColorDeepspace)
                }
            }
        }
    }

    private fun drawPlanets(canvas: Canvas) {
        if (30 > normScale) {
            for (s in GBViewModel.viewStars) {
                if (visible(s.loc.getLoc().x.toInt() * uToS, s.loc.getLoc().y.toInt() * uToS)) {
                    for (p in s.starPlanets) {
                        sP1.set(p.loc.getLoc().x * uToS, p.loc.getLoc().y * uToS)
                        sourceToViewCoord(sP1, vP1)
                        canvas.drawBitmap(
                            bmPlanet!!,
                            vP1.x - bmPlanet!!.getWidth() / 2,
                            vP1.y - bmPlanet!!.getWidth() / 2,
                            null
                        )
                    } // planet loop

                    for (sh in GBViewModel.viewStarShips[s.uid]) {
                        drawShip(canvas, sh, podColorSystem)
                    } // ships loop
                }// if star visible?
            }// star loop
        }
    }

    fun visible(x: Int, y: Int): Boolean {
        rect.set(x - sSystemSize, y - sSystemSize, x + sSystemSize, y + sSystemSize)
        return intersects(rect, vr)
    }

    fun drawShip(canvas: Canvas, sh: GBShip, color: Int) {

        paint.style = Style.STROKE
        val radius = scale * 1f

        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = color

        sP1.set(sh.loc.getLoc().x * uToS, sh.loc.getLoc().y * uToS)
        sourceToViewCoord(sP1, vP1)
        canvas.drawCircle(vP1.x, vP1.y, radius, paint)
        paint.strokeWidth = strokeWidth.toFloat() / 2


        // Don't draw trails zoomed out
        if (normScale > 10 ) {
            return
        }
        paint.color = trailColor
        val iterate = GBViewModel.viewShipTrails[sh.uid].iterator() // TODO waste fewer cycles...

        val alphaFade = paint.alpha / GBViewModel.viewShipTrails[sh.uid].size
        paint.alpha = 0

        var from = GBxy(0f, 0f)

        if (iterate.hasNext()) {
            from = iterate.next()
        }

        while (iterate.hasNext()) {

            val xy = iterate.next()

            sP1.set(from.x * uToS, from.y * uToS)
            sourceToViewCoord(sP1, vP1)
            sP2.set(xy.x * uToS, xy.y * uToS)
            sourceToViewCoord(sP2, vP2)

            canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)

            from = xy
            paint.alpha += alphaFade
        }


    }

    fun drawGrids(canvas: Canvas) {
        // Draw universe grid lines at 250 Universe Coordinates // TODO Why not worky?
        if ((90 < normScale) && (normScale > 80)) {
            paint.color = gridColor
            for (i in 0 until sourceSize step 4500) {
                sP1.set(0f, i.toFloat())
                sP2.set(sourceSize.toFloat(), i.toFloat())
                sourceToViewCoord(sP1, vP1)
                sourceToViewCoord(sP2, vP2)
                canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)
                sP1.set(i.toFloat(), 0f)
                sP2.set(i.toFloat(), sourceSize.toFloat())
                sourceToViewCoord(sP1, vP1)
                sourceToViewCoord(sP2, vP2)
                canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)
            }
        }

        // Draw image grid lines at 1000 coordinates
        if ((75 > normScale) && (normScale > 50)) {
            paint.color = gridColor
            for (i in 0 until sourceSize step 1000) {
                sP1.set(0f, i.toFloat())
                sP2.set(sourceSize.toFloat(), i.toFloat())
                sourceToViewCoord(sP1, vP1)
                sourceToViewCoord(sP2, vP2)
                canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)
                sP1.set(i.toFloat(), 0f)
                sP2.set(i.toFloat(), sourceSize.toFloat())
                sourceToViewCoord(sP1, vP1)
                sourceToViewCoord(sP2, vP2)
                canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)
            }
        }
    }

    fun drawStarsAndCircles(canvas: Canvas) {
        // Always draw stars
        paint.style = Style.STROKE
        for (s in GBViewModel.viewStars) {
            sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
            sourceToViewCoord(sP1, vP1)
            canvas.drawBitmap(bmStar!!, vP1.x - bmStar!!.getWidth() / 2, vP1.y - bmStar!!.getWidth() / 2, null)
        }

        // Draw circles
        if (40 > normScale) {
            paint.style = Style.STROKE
            paint.color = circleColor
            val radius = sSystemSize.toFloat() * scale

            for (s in GBViewModel.viewStars) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
        }


    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        xClick = motionEvent.x
        yClick = motionEvent.y

        sClick = viewToSourceCoord(xClick, yClick)!!

        invalidate()

        return false
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return super.onTouchEvent(event)
//    }

}
