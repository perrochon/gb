package com.zwsi.gb.feature

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.Rect.intersects
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gb.feature.ARBitmaps.Companion.otherBitmap
import com.zwsi.gb.feature.ARBitmaps.Companion.planetBitmap
import com.zwsi.gb.feature.ARBitmaps.Companion.surfaceBitmap
import com.zwsi.gb.feature.GBViewModel.Companion.showClickTargets
import com.zwsi.gb.feature.GBViewModel.Companion.showStats
import com.zwsi.gb.feature.GBViewModel.Companion.superSensors
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.HEADQUARTERS
import com.zwsi.gblib.GBData.Companion.MaxSystemOrbit
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.PlanetOrbit
import com.zwsi.gblib.GBData.Companion.RESEARCH
import com.zwsi.gblib.GBData.Companion.SHUTTLE
import com.zwsi.gblib.GBData.Companion.STATION
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBVector
import com.zwsi.gblib.distance
import java.lang.System.currentTimeMillis
import kotlin.math.*
import kotlin.system.measureNanoTime


fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

class GBClickTarget(var center: PointF, var any: Any) {} // FIXME PERSISTENCE: Should pass in uid. And a type...

class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    // Fields initialized in init
    private var density = 0f
    private var strokeWidth: Int = 0

    private var normScale: Float = 1f // used to make decisions on what to draw at what level
    private val gbDebug = true // show debugbox

    val sourceSize = 18000  // FIXME Would be nice not to hard code here and below
    val universeSize = vm.universeMaxX
    val uToS = sourceSize / universeSize
    val uToSf = uToS.toFloat()
    val sSystemSize = vm.starMaxOrbit * uToS
    val sOrbitSize = 1 * uToS

    // FIXME Do all calculations in Float, and only change to Integer before drawing calls where needed


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

    private var clickTargets = arrayListOf<GBClickTarget>()


    var drawTimes = mutableMapOf<String, Long>()
    var drawStartTimeNanos = 0L
    var drawStartTimeSec = -1
    var lastSec = -1
    var framesThisSec = 0
    var framesLastSec = 0
    var framesMissedThisSec = 0
    var framesMissedLastSec = 0
    var drawUntilStats = 0L
    var lastN = arrayListOf<Long>(120) // TODO: Suspicious that this is computing a running average. Chagnes too fast

    var initTime = 0L

    var numberOfDraws = 0L
    var screenWidthDp = 0
    var screenHeightDp = 0
    var focusSize = 0 // the area where we put stars and planets in the lower half of the screen (left for landscape?)
    var zoomLevelStar = 0f
    var zoomLevelPlanet = 0f
    private var pinnedUidPlanet: Int? = null
    private var pinnedPlanetX = 0f
    private var pinnedPlanetY = 0f

    var turn: Int? = 0  // TODO why nullable

    init {
        initTime = measureNanoTime { initialise() } / 1000000
    }

    private fun initialise() {

        density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

        screenWidthDp = resources.displayMetrics.widthPixels
        screenHeightDp = resources.displayMetrics.heightPixels
        focusSize = min(screenHeightDp / 2, screenWidthDp)

        zoomLevelPlanet = focusSize / PlanetOrbit / 40f
        zoomLevelStar = focusSize / MaxSystemOrbit / 40f


        paint.isAntiAlias = true
        paint.strokeCap = Cap.ROUND
        paint.strokeWidth = strokeWidth.toFloat()


        // set behavior of parent
        setDebug(false)
        maxScale = 30f

        val fullResImage = ImageSource.resource(R.drawable.orion18000)
        val lowResImage = ImageSource.resource(R.drawable.orion1024)
        fullResImage.dimensions(18000, 18000) // FIXME Would be nice not to hard code here and above

        setImage(fullResImage, lowResImage);
        setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
        setDoubleTapZoomScale(zoomLevelStar)
        setScaleAndCenter(
            zoomLevelPlanet,
            PointF(
                vm.race(uidActivePlayer).getHome().loc.getLoc().x * uToS,
                vm.race(uidActivePlayer).getHome().loc.getLoc().y * uToS
            )
        )

        // TODO reset this after recreating the universe

    }


    override fun onDraw(canvas: Canvas) {

        // PERF MapView Drawing Performance: We do star visibility check 4 drawTimes on the whole list.
        //  Saves ~100mus when none are pointVisible. Less when we actually draw

        super.onDraw(canvas)

        // Don't draw anything before image is ready
        if (!isReady) {
            return
        }

        val now = System.nanoTime()
        if ((now - drawStartTimeNanos) / 1000000L > 17L) {
            framesMissedThisSec++
        }
        drawStartTimeNanos = now
        drawStartTimeSec = (drawStartTimeNanos / 1000000000L).rem(60).toInt()
        if (drawStartTimeSec != lastSec) {
            lastSec = drawStartTimeSec
            framesLastSec = framesThisSec
            framesThisSec = 0
            framesMissedLastSec = framesMissedThisSec
            framesMissedThisSec = 0
        }
        framesThisSec++

        numberOfDraws++

        normScale = ((1 / scale) - (1 / maxScale)) / (1 / minScale - 1 / maxScale) * 100

        if (normScale > zoomLevelStar) {
            unpinPlanet()
        }

        visibleFileRect(vr)

        clickTargets.clear()

        // drawTimes["GG"] = measureNanoTime { drawGrids(canvas) }

        drawTimes["dS&C"] = measureNanoTime { drawStarsAndCircles(canvas) }

        drawTimes["dPSF"] = measureNanoTime { drawPlanetSurface(canvas) }

        drawTimes["dP&s"] = measureNanoTime { drawPlanetsAndShips(canvas) }

        drawTimes["dDSs"] = measureNanoTime { drawDeepSpaceShips(canvas) }

        drawTimes["dSNa"] = measureNanoTime { drawStarNames(canvas) }

        drawTimes["dRac"] = measureNanoTime { drawRaces(canvas) }

        drawTimes["dsho"] = measureNanoTime { drawShots(canvas) }

        drawUntilStats = System.nanoTime() - drawStartTimeNanos
        lastN[(numberOfDraws % lastN.size).toInt()] = drawUntilStats


        //if (BuildConfig.SHOWSTATS) {
        if (showStats) {
            drawStats(canvas)
        }

        if (showClickTargets) {
            drawClickTargets(canvas)
        }

        postInvalidateOnAnimation()
//        postInvalidateDelayed(18) // 40 -> ~24 fps, 20 -> 50fps

    } // onDraw

    val statsNamesPaint = TextPaint()

    init {
        statsNamesPaint.textSize = 30f
        statsNamesPaint.setTypeface(Typeface.MONOSPACE);
        statsNamesPaint.setTextAlign(Paint.Align.LEFT);
        statsNamesPaint.style = Style.FILL
        statsNamesPaint.color = debugTextColor
        statsNamesPaint.alpha = 255
    }

    private fun drawStats(canvas: Canvas) {
        if (gbDebug) {

            var l = 1f
            val h = 50

            canvas.drawText(
                "MS:${maxScale.toInt()}|mS:${minScale.f(3)}|DY:${density.toInt()}" +
                        "|NS:${normScale.f(2)}|SC:${scale.f(2)}", 8f, l++ * h, statsNamesPaint
            )
//            canvas.drawText(
//                "UCenter: ${center!!.x.toInt() / uToS}, ${center!!.y.toInt() / uToS} / "
//                        + "SCenter: ${center!!.x.toInt()}, ${center!!.y.toInt()}", 8f, l++ * h, statsNamesPaint
//            )
//            canvas.drawText(
//                "Uvisible: ${(vr.right - vr.left) / uToS}x${(vr.bottom - vr.top) / uToS}",
//                8f,
//                l++ * h,
//                statsNamesPaint
//            )
//            canvas.drawText(
//                "Svisible: ${(vr.right - vr.left)}x${(vr.bottom - vr.top)}" + " at " + vr,
//                8f,
//                l++ * h,
//                statsNamesPaint
//            )
//            canvas.drawText("Screen Click: ($xClick, $yClick)", 8f, l++ * h, statsNamesPaint)
//            canvas.drawText("Source Click: (${sClick.x},${sClick.y})", 8f, l++ * h, statsNamesPaint)
//            canvas.drawText(
//                "Universe Click: (${sClick.x / uToS},${sClick.y / uToS})", 8f, l++ * h, statsNamesPaint
//            )
            // Turn Stats
            canvas.drawText(
                "TU:${turn!!.f(4)}" +
                        "|As:${vm.ships.size.f(4)}" +
                        "|Ds:${vm.deepSpaceUidShips.size.f(4)}" +
                        "|sh:${vm.shots.size.f(3)}",
                8f,
                l++ * h,
                statsNamesPaint
            )
//            // Memory Stats
//            canvas.drawText(
//                "MM:${(Runtime.getRuntime().maxMemory() / 1048576).f(3)}" +
//                        "|TM:${(Runtime.getRuntime().totalMemory() / 1048576).f(3)}" +
//                        "|UM:${((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576).f(3)}" +
//                        "|FM:${(Runtime.getRuntime().freeMemory() / 1048576).f(3)}",
//                8f,
//                l++ * h,
//                statsNamesPaint
//            )
            // Performance Stats
            canvas.drawText(
                "Do:${(GBViewModel.timeLastTurn / 1000000L).f(3)}" +
                        "|TJ:${(GBViewModel.timeLastToJSON / 1000000L).f(3)}" +
                        "|FW:${(GBViewModel.timeFileWrite / 1000000L).f(2)}" +
                        "|LL:${(GBViewModel.timeLastLoad / 1000000L).f(2)}" + // Rare event. We don't care so much
                        "|FJ:${(GBViewModel.timeFromJson / 1000000L).f(2)}" +
                        "|MU:${(GBViewModel.timeModelUpdate / 1000000L).f(2)}",
                8f,
                l++ * h,
                statsNamesPaint
            )

            canvas.drawText(
                "Draw:${(lastN.average() / 1000000).toInt().f(2)}ms" +
                        "|fps:${framesLastSec.f(3)}" +
                        "|fms:${framesMissedLastSec.f(3)}" +
                        "|init:${initTime.f(5)}ms",
                8f,
                l++ * h,
                statsNamesPaint
            )

//            GBViewModel.drawTimes.forEach { t, u -> canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, statsNamesPaint) }

            drawTimes.forEach { t, u ->
                canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, statsNamesPaint)
            }

            ARBitmaps.initTimes.forEach { t, u ->
                canvas.drawText("$t:${(u / 1000L).f(5)}μs", 8f, l++ * h, statsNamesPaint)
            }

        }
    }


    private fun drawRaces(canvas: Canvas) {
        // Timing Info:  no race 200μs, 1 race 400μs, more ?μs
        if (normScale > 10) {

            for ((_, r) in vm.races) {

                if (superSensors || vm.race(uidActivePlayer).raceVisibleStars.contains(vm.planet(r.uidHomePlanet).uidStar)) {

                    if (pointVisible(
                            r.getHome().star.loc.getLoc().x * uToSf,
                            r.getHome().star.loc.getLoc().y * uToSf
                        )
                    ) {
                        sP1.set(r.getHome().star.loc.getLoc().x * uToSf + 50, r.getHome().star.loc.getLoc().y * uToSf)
                        sourceToViewCoord(sP1, vP1)
                        canvas.drawBitmap(r.getBitmap(), vP1.x, vP1.y, null)
                    }
                }
            }
        }
    }

    var shotPaint = Paint()

    init {
        shotPaint.strokeWidth = strokeWidth.toFloat() / 4
    }

    private fun drawShots(canvas: Canvas) {
        if (40 > normScale) {

            if (true) {
                for (shot: GBVector in vm.shots) {
                    if (pointVisible(shot.from.x * uToSf, shot.from.y * uToSf) ||
                        pointVisible(shot.to.x * uToSf, shot.to.y * uToSf)
                    ) {
                        shotPaint.color = vm.race(shot.uidRace).getColor() // TODO PERFORMANCE add color when making shots as an int
                        val shotduration = 333
                        val distance = shot.from.distance(shot.to) * uToS * scale * 2f
                        val milis = currentTimeMillis().rem(shotduration).toFloat()
                        val shotFront = milis * distance / shotduration
                        if (milis < 50f) {
                            shotPaint.setPathEffect(DashPathEffect(floatArrayOf(shotFront, Float.MAX_VALUE), 0f))
                        } else {
                            val shotend = (milis - 50f) * distance / shotduration
                            val shotlength = 50f * distance / shotduration
                            shotPaint.setPathEffect(
                                DashPathEffect(
                                    floatArrayOf(
                                        0f,
                                        shotend,
                                        shotlength,
                                        Float.MAX_VALUE
                                    ), 0f
                                )
                            )

                        }
                        sP1.set(shot.from.x * uToS, shot.from.y * uToS)
                        sourceToViewCoord(sP1, vP1)
                        sP2.set(shot.to.x * uToS, shot.to.y * uToS)
                        sourceToViewCoord(sP2, vP2)
                        canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, shotPaint)
                    }
                }
            }
        }
    }

    val starNamesPaint = TextPaint()

    init {
        starNamesPaint.textSize = 50f
        starNamesPaint.setTextAlign(Paint.Align.CENTER);
        starNamesPaint.style = Style.FILL
        starNamesPaint.color = labelColor
        starNamesPaint.alpha = 128
    }

    private fun drawStarNames(canvas: Canvas) {
        // TODO PERFORMANCE drawText() is slow: no star 20μs, 1 star 50μs, 2 stars 50μs 15 stars 300μs
        for ((_, s) in vm.stars) {
            val loc = s.loc.getLoc()
            if (pointVisible(loc.x * uToSf, loc.y * uToSf)) {
                sP1.set(loc.x * uToSf, loc.y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawText(s.name, vP1.x, vP1.y - 45, starNamesPaint)
            }
        }
    }

    private fun drawDeepSpaceShips(canvas: Canvas) {
        // Timing Info:  no shipsData 300μs, 50 shipsData  2000μs, 500 shipsData 900μs (at beginning)

        if (101 >= normScale) {
            for (sh in vm.deepSpaceUidShips.map { vm.ship(it) }) {
                if (superSensors || sh.race.uid == 0) { // Fog of War
                    if (pointVisible(
                            sh.loc.getLoc().x * uToSf,
                            sh.loc.getLoc().y * uToSf
                        )
                    ) {
                        drawShip(canvas, sh)
                    }
                }
            }
        }
    }

    private fun drawPlanetSurface(canvas: Canvas) {

        if (2 > normScale) {
            for ((_, s) in vm.stars) {

                if (superSensors || vm.race(uidActivePlayer).raceVisibleStars.contains(s.uid)) {

                    if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                        for (uidP in s.starUidPlanets) { // PERF only draw one...
                            val p = vm.planet(uidP)
                            if (planetVisible(
                                    p.loc.getLoc().x * uToSf,
                                    p.loc.getLoc().y * uToSf
                                )
                            ) { // FIXME do this in Float

                                sP1.set(p.loc.getLoc().x * uToS, p.loc.getLoc().y * uToS)
                                sourceToViewCoord(sP1, vP1)

                                for (j in 0 until p.sectors.size) {

                                    val o = (PlanetOrbit * 0.4f) * uToS * scale
                                    val size = 4 * o / p.width
                                    canvas.drawBitmap(
                                        surfaceBitmap(p.sectors[j].type),
                                        null,
                                        RectF(
                                            vP1.x - 2 * o + p.sectorX(j) * size,
                                            vP1.y - o + p.sectorY(j) * size,
                                            vP1.x - 2 * o + p.sectorX(j) * size + size,
                                            vP1.y - o + p.sectorY(j) * size + size
                                        ),
                                        null
                                    )

                                    if (p.sectors[j].population > 0) {
                                        val fill =
                                            p.sectors[j].population.toFloat() / p.sectors[j].maxPopulation.toFloat()
                                        paint.style = Style.STROKE
                                        paint.color = p.sectors[j].sectorOwner.getColor()
                                        paint.strokeWidth = strokeWidth.toFloat()
                                        canvas.drawLine(
                                            vP1.x - 2 * o + p.sectorX(j) * size + size / 10f,
                                            vP1.y - o + p.sectorY(j) * size + size * 0.9f,
                                            vP1.x - 2 * o + p.sectorX(j) * size + size / 10f + fill * size * 0.8f,
                                            vP1.y - o + p.sectorY(j) * size + size * 0.9f, paint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun drawPlanetsAndShips(canvas: Canvas) {
        for ((_, s) in vm.stars) {

            if (superSensors || vm.race(uidActivePlayer).raceVisibleStars.contains(s.uid)) {

                if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {

                    for (uidP in s.starUidPlanets) {
                        val p = vm.planet(uidP)

                        if (30 > normScale) {

                            sP1.set(p.loc.getLoc().x * uToS, p.loc.getLoc().y * uToS)
                            sourceToViewCoord(sP1, vP1)
                            if (normScale > 2) {
                                val planetBitmap = planetBitmap(p.idxtype)
                                val planetSize = 1.6f * uToSf * scale // FIXME - move outside loops...
                                canvas.drawBitmap(
                                    planetBitmap,
                                    null,
                                    RectF(
                                        vP1.x - planetSize,
                                        vP1.y - planetSize,
                                        vP1.x + planetSize,
                                        vP1.y + planetSize
                                    ),
                                    null
                                )
                            }
                            clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y), p))

                            // planet names
                            if (4 > normScale) {
                                paint.textSize = 50f
                                paint.setTextAlign(Paint.Align.CENTER);
                                paint.style = Style.FILL
                                paint.color = labelColor
                                paint.alpha = 128
                                sP1.set(p.loc.getLoc().x * uToSf, p.loc.getLoc().y * uToSf)
                                sourceToViewCoord(sP1, vP1)
                                val o = (PlanetOrbit * 0.4f) * uToS * scale
                                canvas.drawText(p.name, vP1.x, vP1.y - o * 1.1f, paint)
                                clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y - o * 1.1f), p))
                            }


                            // planet orbit circles and surface rectangles
                            if (3 > normScale) {
                                paint.style = Style.STROKE // FIXME move this outside.
                                paint.color = circleColor
                                paint.strokeWidth = strokeWidth.toFloat()
                                val radius = vm.planetOrbit * uToS * scale
                                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
                            }
                            if (2 > normScale) {
                                paint.style = Style.STROKE
                                paint.color = circleColor
                                paint.strokeWidth = strokeWidth.toFloat()
                                val o = (PlanetOrbit * 0.4f) * uToS * scale
                                canvas.drawRect(vP1.x - 2 * o, vP1.y - o, vP1.x + 2 * o, vP1.y + o, paint)

                            }
                        } // if scale

                        // Draw orbit shipsData
                        for (uidS in p.orbitUidShips) {
                            val sh = vm.ship(uidS)
                            drawShip(canvas, sh)
                        }

                        // Draw landed shipsData

                        for (uidS in p.landedUidShips) {
                            val sh = vm.ship(uidS)
                            drawShip(canvas, sh)
                        }

                    } // planet loop

                    for (uidPp in s.starUidPatrolPoints) {
                        val pp = vm.patrolPoint(uidPp)
                        sP1.set(pp.loc.getLoc().x * uToS, pp.loc.getLoc().y * uToS)
                        sourceToViewCoord(sP1, vP1)

                        // patrol point circles
//                        if (20 > normScale) {
//                            paint.style = Style.STROKE
//                            paint.color = circleColor
//                            paint.strokeWidth = strokeWidth.toFloat()
//                            val radius = vm.starMaxOrbit * 0.5f * uToS * scale
//                            canvas.drawCircle(vP1.x, vP1.y, radius, paint)
//
//                        }

                        // Draw orbit shipsData
                        for (uidS in pp.orbitUidShips) {
                            val sh = vm.ship(uidS)
                            drawShip(canvas, sh)
                        }
                    } // patrol point loop


                    // Draw In System Ship
                    for (uidS in s.starUidShips) {
                        val sh = vm.ship(uidS)
                        drawShip(canvas, sh)
                    } // shipsData loop

                }// if star pointVisible?
            }
        }// star loop
    }

    fun pointVisible(x: Float, y: Float): Boolean {
        rect.set(
            (x - sSystemSize.toFloat()).toInt(),
            (y - sSystemSize.toFloat()).toInt(),
            (x + sSystemSize.toFloat()).toInt(),
            (y + sSystemSize.toFloat()).toInt()
        )
        return intersects(rect, vr)
    }

    fun planetVisible(x: Float, y: Float): Boolean {
        rect.set(
            (x - sOrbitSize.toFloat()).toInt(),
            (y - sOrbitSize.toFloat()).toInt(),
            (x + sOrbitSize.toFloat()).toInt(),
            (y + sOrbitSize.toFloat()).toInt()
        )
        return intersects(rect, vr)
    }

    private val shipPaint = Paint()

    init {
        shipPaint.style = Style.STROKE
        shipPaint.strokeCap = Cap.ROUND
        shipPaint.isAntiAlias = true
        shipPaint.strokeWidth = strokeWidth.toFloat()
    }

    fun drawShip(canvas: Canvas, sh: GBShip) {

        val radius = scale * 1.6f

        if (sh.health <= 0) {
            shipPaint.color = deadColor
        } else {
            shipPaint.color = sh.race.getColor()
        }
        if (sh.loc.level == DEEPSPACE) {
            shipPaint.alpha = 128
        }

        sP1.set(sh.loc.getVMLoc(vm).x * uToS, sh.loc.getVMLoc(vm).y * uToS)
        sourceToViewCoord(sP1, vP1)

        if (normScale >= 10) {
            canvas.drawPoint(vP1.x, vP1.y, shipPaint)
        }

        if (10 > normScale) {
            // Draw animation
            if (sh.health > 0 && sh.idxtype != STATION) {
                var r = radius
                if (sh.idxtype == BATTLESTAR) {
                    r = r * 1.5f
                }
                val theta: Float = currentTimeMillis().rem(1000).toFloat() * 2f * PI.toFloat() / 1000
                canvas.drawCircle(
                    vP1.x + cos(theta) * r,
                    vP1.y + sin(theta) * r,
                    r / 10,
                    shipPaint
                )
            }
            // Draw circle / square
            when (sh.idxtype) {
                POD, CRUISER, SHUTTLE, STATION -> {
                    canvas.drawCircle(vP1.x, vP1.y, radius, shipPaint)
                }
                BATTLESTAR -> {
                    canvas.drawCircle(vP1.x, vP1.y, radius * 1.5f, shipPaint)
                }
                FACTORY, RESEARCH, HEADQUARTERS -> {
                    canvas.drawRect(vP1.x - radius, vP1.y - radius, vP1.x + radius, vP1.y + radius, shipPaint)
                }
            }

            drawTrails(canvas, sh)

        }

        // Draw bitmap
        if (1 > normScale) {
            drawShipBitmap(canvas, sh.getBitmap(), false, radius)
        }

        if (5 > normScale) {
            clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y), sh))
        }

    }

    // FIXME Inline this?
    private fun drawShipBitmap(canvas: Canvas, bitmap: Bitmap, circle: Boolean, radius: Float) {

//        if (circle) { // FIXME remove code and parameter circle
//            canvas.drawCircle(vP1.x, vP1.y, radius, shipPaint)
//        } else {
//            canvas.drawRect(vP1.x - radius, vP1.y - radius, vP1.x + radius, vP1.y + radius, shipPaint)
//        }
        val o = bitmap.width / 2.4f / 100 * scale // TODO PERF not look this up each time?
        canvas.drawBitmap(
            bitmap, null,
            RectF(vP1.x - o, vP1.y - o, vP1.x + o, vP1.y + o), null
        )

    }

    private val trailPaint = Paint()
    private val maxTrailAlpha: Int

    init {
        trailPaint.strokeWidth = strokeWidth.toFloat() / 2
        trailPaint.strokeJoin = Paint.Join.ROUND
        trailPaint.strokeCap = Cap.BUTT
        trailPaint.color = trailColor
        maxTrailAlpha = trailPaint.alpha
    }

    fun drawTrails(canvas: Canvas, sh: GBShip) {

        if (sh.trails.size > 1) {
            val alphaFade = maxTrailAlpha / (sh.trails.size - 1)
            trailPaint.alpha = alphaFade

            val iterate = sh.trails.iterator()

            val from = iterate.next()
            sP1.set(from.x * uToS, from.y * uToS)
            sourceToViewCoord(sP1, vP1)

            while (iterate.hasNext()) {

                val to = iterate.next()
                sP2.set(to.x * uToS, to.y * uToS)
                sourceToViewCoord(sP2, vP2)

                canvas.drawLine(vP1.x, vP1.y, vP2.x, vP2.y, trailPaint)

                vP1.set(vP2)
                trailPaint.alpha += alphaFade
            }
        }
    }

    fun drawGrids(canvas: Canvas) {
        // Draw universe grid lines at 250 Universe Coordinates
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

    fun pinPlanet(uidP: Int) {
        pinnedUidPlanet = uidP
        pinnedPlanetX = vm.planets[uidP]!!.loc.getLoc().x
        pinnedPlanetY = vm.planets[uidP]!!.loc.getLoc().y
    }

    fun unpinPlanet() {
        pinnedUidPlanet = null
    }

    fun shiftToPinnedPlanet() {
        if (pinnedUidPlanet != null) {
            val p = vm.planets[pinnedUidPlanet!!]!!
            val dx = (p.loc.getLoc().x - pinnedPlanetX) * uToS
            val dy = (p.loc.getLoc().y - pinnedPlanetY) * uToS
            setScaleAndCenter(
                getScale(),
                PointF(
                    center!!.x + dx,
                    center!!.y + dy
                )
            )
            pinnedPlanetX = p.loc.getLoc().x
            pinnedPlanetY = p.loc.getLoc().y
        }
    }


    fun drawStarsAndCircles(canvas: Canvas) {
        // Always draw stars

        paint.style = Style.STROKE // FIXME use dedicated paint
        val starBitmap = otherBitmap(R.drawable.star)
        val halfSize = starBitmap.getWidth() / 2
        for ((_, s) in vm.stars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawBitmap(starBitmap, vP1.x - halfSize, vP1.y - halfSize, null)
                clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y), s))

            }
        }

        // Draw circles
        // was if (40 > normScale) {
        paint.style = Style.STROKE
        paint.color = circleColor
        paint.strokeWidth = strokeWidth.toFloat()
        val radius = sSystemSize.toFloat() * scale

        for ((_, s) in vm.stars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
        }

    }

    fun drawClickTargets(canvas: Canvas) {

        paint.style = Style.STROKE
        paint.color = Color.parseColor("#8055bb33")  //
        paint.strokeWidth = strokeWidth.toFloat()
        val radius = 80f

        clickTargets.forEach { canvas.drawCircle(it.center.x, it.center.y, radius, paint) }


    }


    // FIXME onTouchEvent override doesn't seem to do much other than invalidate. Do we need it?
    override fun onTouchEvent(event: MotionEvent): Boolean {
        xClick = event.x
        yClick = event.y

        if (isReady) {
            sClick = viewToSourceCoord(xClick, yClick)!! // FIXME what does this do?
            invalidate()

        }
        return super.onTouchEvent(event)
    }

    fun clickTarget(event: MotionEvent): Any? {
        val x = event.x
        val y = event.y

        val closest =
            clickTargets.minBy { (it.center.x - x) * (it.center.x - x) + (it.center.y - y) * (it.center.y - y) }

        if ((closest != null)) {
            val distance =
                sqrt((closest.center.x - x) * (closest.center.x - x) + (closest.center.y - y) * (closest.center.y - y))
            if (distance < 80f) { //FIXME Make  Clicktarget size a constant. vClickTargetRadius
                return closest.any
            } else {
                val closestPlanet = clickTargets.filter { it.any is GBPlanet }
                    .minBy { (it.center.x - x) * (it.center.x - x) + (it.center.y - y) * (it.center.y - y) }
                if (closestPlanet != null) {
                    val distance2 =
                        sqrt(
                            (closestPlanet.center.x - x) * (closestPlanet.center.x - x)
                                    + (closestPlanet.center.y - y) * (closestPlanet.center.y - y)
                        )
                    if (distance2 < (vm.planetOrbit * uToSf * scale)) {
                        return closestPlanet.any
                    }
                }
            }
        }
        return null
    }

}
