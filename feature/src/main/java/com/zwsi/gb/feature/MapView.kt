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
import com.zwsi.gb.feature.GBViewModel.Companion.viewShipTrails
import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBShip
import kotlin.system.measureNanoTime

//TODO where should these extensions to basic types live?
fun Double.f(digits: Int) = java.lang.String.format("%.${digits}f", this)
fun Float.f(digits: Int) = java.lang.String.format("%.${digits}f", this)
fun Int.f(digits: Int) = java.lang.String.format("%${digits}d", this)
fun Long.f(digits: Int) = java.lang.String.format("%${digits}d", this)

class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr), View.OnTouchListener {

    // Fields initialized in init
    private var density = 0f
    private var strokeWidth: Int = 0

    private var normScale: Float = 1f // used to make decisions on what to draw at what level
    private val gbDebug = true // show debugbox

    private var bmStar: Bitmap? = null
    private var bmPlanet: Bitmap? = null
    private var bmRaceXenos: Bitmap? = null
    private var bmRaceImpi: Bitmap? = null
    private var bmRaceBeetle: Bitmap? = null
    private var bmRaceTortoise: Bitmap? = null

    val sourceSize = 18000 // TODO get from elsewhere
    val universeSize = universe.universeMaxX
    val uToS = sourceSize / universeSize
    val uToSf = uToS.toFloat()
    val sSystemSize = universe.systemBoundary * uToS


    // Object creation outside onDraw. These are only used in onDraw, but here for performance reasons?
    private val paint = Paint()
    private val debugTextColor = Color.parseColor("#FFffbb33")
    private val labelColor = Color.parseColor("#FFffbb33")
    private val podColorSystem = Color.parseColor("#ffee1111")
    private val deadColor = Color.parseColor("#ffffffff")
    private val trailColor = Color.parseColor("#40bbbbbb")
    private val gridColor = Color.parseColor("#10bbbbbb")
    private val circleColor = Color.parseColor("#20FF6015")
    private val shotColor = Color.parseColor("#FFFF0000")

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

        bmRaceXenos = BitmapFactory.decodeResource(getResources(), R.drawable.xenost)!!
        w = density / 420f * bmRaceXenos!!.getWidth() / 30
        h = density / 420f * bmRaceXenos!!.getHeight() / 30
        bmRaceXenos = Bitmap.createScaledBitmap(bmRaceXenos!!, w.toInt(), h.toInt(), true)!!

        bmRaceImpi = BitmapFactory.decodeResource(getResources(), R.drawable.impit)!!
        w = density / 420f * bmRaceImpi!!.getWidth() / 30
        h = density / 420f * bmRaceImpi!!.getHeight() / 30
        bmRaceImpi = Bitmap.createScaledBitmap(bmRaceImpi!!, w.toInt(), h.toInt(), true)!!

        bmRaceBeetle = BitmapFactory.decodeResource(getResources(), R.drawable.beetle)!!
        w = density / 420f * bmRaceBeetle!!.getWidth() / 30
        h = density / 420f * bmRaceBeetle!!.getHeight() / 30
        bmRaceBeetle = Bitmap.createScaledBitmap(bmRaceBeetle!!, w.toInt(), h.toInt(), true)!!

        bmRaceTortoise = BitmapFactory.decodeResource(getResources(), R.drawable.tortoise)!!
        w = density / 420f * bmRaceTortoise!!.getWidth() / 30
        h = density / 420f * bmRaceTortoise!!.getHeight() / 30
        bmRaceTortoise = Bitmap.createScaledBitmap(bmRaceTortoise!!, w.toInt(), h.toInt(), true)!!

        setOnTouchListener(this);

        // set behavior of parent
        setDebug(false)
        maxScale = 12f

        GBViewModel.mapView = this

    }

    override fun onDraw(canvas: Canvas) {

        // TODO MapView Drawing Performance: We do star visibility check 4 times on the whole list.
        //  Saves ~100mus when none are visible. Less when we actually draw

        // Don't show the tiles on high zoom, as it's blurry anyway
        // Problem with this may be that clicks no longer get handled propertly
//        if (scale < 8)
        super.onDraw(canvas)

        // Don't draw anything before image is ready
        if (!isReady) {
            return
        }

        startTimeNanos = System.nanoTime()
        numberOfDraws++

        normScale = ((1 / scale) - (1 / maxScale)) / (1 / minScale - 1 / maxScale) * 100

        visibleFileRect(vr)

        times["GG"] = measureNanoTime { drawGrids(canvas) }

        times["SC"] = measureNanoTime { drawStarsAndCircles(canvas) }

        times["PS"] = measureNanoTime { drawPlanetsAndShips(canvas) }

        times["DS"] = measureNanoTime { drawDeepSpaceShips(canvas) }

        times["SN"] = measureNanoTime { drawStarNames(canvas) }

        times["Ra"] = measureNanoTime { drawRaces(canvas) }

        times["Sh"] = measureNanoTime { drawShots(canvas) }


        drawUntilStats = System.nanoTime() - startTimeNanos
        last20[(numberOfDraws % last20.size).toInt()] = drawUntilStats

        drawStats(canvas)


    } // onDraw

    private fun drawStats(canvas: Canvas) {
        if (gbDebug) {

            paint.textSize = 40f
            paint.setTypeface(Typeface.MONOSPACE);
            paint.style = Style.FILL
            paint.color = Color.parseColor("#80ffbb33") // TODO get color holo orange with alpha
            paint.color = debugTextColor
            paint.alpha = 255

            var l = 1f
            val h = 50

            //            canvas.drawText("maxScale: $maxScale / minScale: $minScale / density: $density", 8f, l++ * h, paint)
            canvas.drawText("Norm: ${normScale.f(3)} | Scale: ${scale.f(3)}", 8f, l++ * h, paint)
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
                "A: ${GBViewModel.viewShips.size} | D: ${GBViewModel.viewDeepSpaceShips.size} | +: ${GBViewModel.viewDeadShips.size}",
                8f,
                l++ * h,
                paint
            )
            canvas.drawText(
                "Turn:${universe.turn.f(4)} | View:${(GBViewModel.timeModelUpdate / 1000L).f(5)}μs | Backend:${(GBViewModel.timeLastTurn / 1000L).f(6)}μs",
                8f,
                l++ * h,
                paint
            )
            canvas.drawText(
                "DT: ${(last20.average()!! / 1000).toInt().f(4)}μs",
                8f,
                l++ * h,
                paint
            )


            GBViewModel.times.forEach { t, u -> canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, paint) }

            times.forEach { t, u -> canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, paint) }

        }
    }

    private fun drawRaces(canvas: Canvas) {
        // Timing Info:  no race 200μs, 1 race 400μs, more ?μs
        if (normScale > 50) {

            for (r in GBViewModel.viewRaces) {
                if (visible(r.home.star.loc.getLoc().x.toInt() * uToS, r.home.star.loc.getLoc().y.toInt() * uToS)) {
                    sP1.set(r.home.star.loc.getLoc().x * uToSf + 50, r.home.star.loc.getLoc().y * uToSf)
                    sourceToViewCoord(sP1, vP1)
                    when (r.idx) {
                        0 -> {
                            canvas.drawBitmap(bmRaceXenos!!, vP1.x, vP1.y, null)
                        }
                        1 -> {
                            canvas.drawBitmap(bmRaceImpi!!, vP1.x, vP1.y, null)
                        }
                        2 -> {
                            canvas.drawBitmap(bmRaceBeetle!!, vP1.x, vP1.y, null)
                        }
                        3 -> {
                            canvas.drawBitmap(bmRaceTortoise!!, vP1.x, vP1.y, null)
                        }
                    }
                }
            }
        }
    }

    private fun drawShots(canvas: Canvas) {
        if (30 > normScale) {

            if (true) {
                for (shot in GBViewModel.viewShots) {
                    paint.color = shotColor
                    paint.strokeWidth = strokeWidth.toFloat() / 4
                    if (visible(shot.from.x.toInt() * uToS, shot.from.y.toInt() * uToS) ||
                        visible(shot.to.x.toInt() * uToS, shot.to.y.toInt() * uToS)
                    ) {

                        sP1.set(shot.from.x * uToS, shot.from.y * uToS)
                        sourceToViewCoord(sP1, vP1)
                        sP2.set(shot.to.x * uToS, shot.to.y * uToS)
                        sourceToViewCoord(sP2, vP2)

                        canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)
                    }
                }
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
            for (sh in GBViewModel.viewDeepSpaceShips) {
                if (visible(
                        sh.loc.getLoc().x.toInt() * uToS,
                        sh.loc.getLoc().y.toInt() * uToS
                    )
                ) {
                    paint.color = Color.parseColor(sh.race.color)
                    paint.alpha = 128
                    drawShip(canvas, sh)
                }
            }
        }
    }

    private fun drawPlanetsAndShips(canvas: Canvas) {
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

                        for (sh in GBViewModel.viewOrbitShips[p.uid]) {
                            paint.alpha = 128
                            paint.color = Color.parseColor(sh.race.color)
                            drawShip(canvas, sh)
                        }


                    } // planet loop

                    for (sh in GBViewModel.viewStarShips[s.uid]) {
                        paint.alpha = 255
                        paint.color = Color.parseColor(sh.race.color)
                        drawShip(canvas, sh)
                    } // ships loop
                }// if star visible?
            }// star loop
        }
    }

    fun visible(x: Int, y: Int): Boolean {
        rect.set(x - sSystemSize, y - sSystemSize, x + sSystemSize, y + sSystemSize)
        return intersects(rect, vr)
    }

    fun drawShip(canvas: Canvas, sh: GBShip) {

        paint.style = Style.STROKE
        val radius = scale * 1f

        paint.strokeWidth = strokeWidth.toFloat()

        if (sh.health == 0) {
            paint.color = deadColor
        }

        sP1.set(sh.loc.getLoc().x * uToS, sh.loc.getLoc().y * uToS)
        sourceToViewCoord(sP1, vP1)
        when (sh.idxtype) {

            POD -> {
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
            CRUISER -> {
                canvas.drawRect(vP1.x - radius, vP1.y - radius, vP1.x + radius, vP1.y + radius, paint)
            }
            else -> {
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
        }


        // Don't draw trails zoomed out
        if (normScale > 10) {
            return
        }

        paint.strokeWidth = strokeWidth.toFloat() / 2
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Cap.BUTT
        paint.color = trailColor
        val alphaFade = paint.alpha / viewShipTrails[sh.uid].size
        paint.alpha = 0

        val iterate = GBViewModel.viewShipTrails[sh.uid].iterator()

        var from = iterate.next()
        sP1.set(from.x * uToS, from.y * uToS)
        sourceToViewCoord(sP1, vP1)

        while (iterate.hasNext()) {

            val to = iterate.next()
            sP2.set(to.x * uToS, to.y * uToS)
            sourceToViewCoord(sP2, vP2)

            canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, paint)

            vP1.set(vP2)
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
            if (visible(s.loc.getLoc().x.toInt() * uToS, s.loc.getLoc().y.toInt() * uToS)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawBitmap(bmStar!!, vP1.x - bmStar!!.getWidth() / 2, vP1.y - bmStar!!.getWidth() / 2, null)
            }
        }

        // Draw circles
        if (40 > normScale) {
            paint.style = Style.STROKE
            paint.color = circleColor
            paint.strokeWidth = strokeWidth.toFloat()
            val radius = sSystemSize.toFloat() * scale

            for (s in GBViewModel.viewStars) {
                if (visible(s.loc.getLoc().x.toInt() * uToS, s.loc.getLoc().y.toInt() * uToS)) {
                    sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                    sourceToViewCoord(sP1, vP1)
                    canvas.drawCircle(vP1.x, vP1.y, radius, paint)
                }
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
