package com.zwsi.gb.feature

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.Rect.intersects
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gb.feature.GBViewModel.Companion.gi
import com.zwsi.gb.feature.GBViewModel.Companion.viewPlanets
import com.zwsi.gb.feature.GBViewModel.Companion.viewShipTrails
import com.zwsi.gb.feature.GBViewModel.Companion.viewStarPlanets
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.MaxSystemOrbit
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBVector
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

//TODO where should these extensions to basic types live?
fun Double.f(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun Float.f(digits: Int) = java.lang.String.format("%.${digits}f", this)
fun Int.f(digits: Int) = java.lang.String.format("%${digits}d", this)
fun Long.f(digits: Int) = java.lang.String.format("%${digits}d", this)

class GBClickTarget(var center: PointF, var any: Any) {}

class MapView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

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

    private var bmASurface = HashMap<Int, Bitmap>()

    private val bitmaps = HashMap<Int, Bitmap>()


    val sourceSize = 18000  // FIXME Would be nice not to hard code here and below
    val universeSize = gi.universeMaxX
    val uToS = sourceSize / universeSize
    val uToSf = uToS.toFloat()
    val sSystemSize = gi.systemBoundary * uToS
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


    var times = mutableMapOf<String, Long>()
    var startTimeNanos = 0L
    var drawUntilStats = 0L
    var last20 = arrayListOf<Long>(60)
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
        initialise()
    }

    private fun initialise() {

        density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

        screenWidthDp = resources.displayMetrics.widthPixels
        screenHeightDp = resources.displayMetrics.heightPixels
        focusSize = min(screenHeightDp / 2, screenWidthDp)

        zoomLevelPlanet = focusSize / PlanetaryOrbit / 40f
        zoomLevelStar = focusSize / MaxSystemOrbit / 40f


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

        // Do a better way. If it works, we replace above... Neet do figure out planet/star, where we divide by 2/1
        val drawables = listOf<Int>(R.drawable.podt, R.drawable.cruisert, R.drawable.factory, R.drawable.beetlepod)
        for (i in drawables) {
            val bm = BitmapFactory.decodeResource(getResources(), i)!!
            w = density / 420f * bm.getWidth() / 60
            h = density / 420f * bm.getHeight() / 60
            bitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
        }

        bmASurface[3] = BitmapFactory.decodeResource(getResources(), R.drawable.desert)!!
        bmASurface[5] = BitmapFactory.decodeResource(getResources(), R.drawable.forest)!!
        bmASurface[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gas)!!
        bmASurface[6] = BitmapFactory.decodeResource(getResources(), R.drawable.ice)!!
        bmASurface[1] = BitmapFactory.decodeResource(getResources(), R.drawable.land)!!
        bmASurface[4] = BitmapFactory.decodeResource(getResources(), R.drawable.mountain)!!
        bmASurface[7] = BitmapFactory.decodeResource(getResources(), R.drawable.rock)!!
        bmASurface[0] = BitmapFactory.decodeResource(getResources(), R.drawable.water)!!

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
                com.zwsi.gb.feature.GBViewModel.viewRaces.toList().component1().second.getHome().loc.getLoc().x * uToS,
                com.zwsi.gb.feature.GBViewModel.viewRaces.toList().component1().second.getHome().loc.getLoc().y * uToS
            )
        )

        // TODO reset this after recreating the universe

    }


    override fun onDraw(canvas: Canvas) {

        // PERF MapView Drawing Performance: We do star visibility check 4 times on the whole list.
        //  Saves ~100mus when none are pointVisible. Less when we actually draw

        super.onDraw(canvas)

        // Don't draw anything before image is ready
        if (!isReady) {
            return
        }

        startTimeNanos = System.nanoTime()
        numberOfDraws++

        normScale = ((1 / scale) - (1 / maxScale)) / (1 / minScale - 1 / maxScale) * 100

        if (normScale > 4) {
            unpinPlanet()
        }

        visibleFileRect(vr)

        clickTargets.clear()

        // times["GG"] = measureNanoTime { drawGrids(canvas) }

        times["dS&C"] = measureNanoTime { drawStarsAndCircles(canvas) }

        times["dPSF"] = measureNanoTime { drawPlanetSurface(canvas) }

        times["dP&s"] = measureNanoTime { drawPlanetsAndShips(canvas) }

        times["dDSs"] = measureNanoTime { drawDeepSpaceShips(canvas) }

        times["dSNa"] = measureNanoTime { drawStarNames(canvas) }

        times["dRac"] = measureNanoTime { drawRaces(canvas) }

        times["dsho"] = measureNanoTime { drawShots(canvas) }

        drawUntilStats = System.nanoTime() - startTimeNanos
        last20[(numberOfDraws % last20.size).toInt()] = drawUntilStats

        if (BuildConfig.SHOWSTATS) {
            drawStats(canvas)
        }

        //drawClickTargets(canvas)


    } // onDraw

    private fun drawStats(canvas: Canvas) {
        if (gbDebug) {

            paint.textSize = 40f
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.style = Style.FILL
            paint.color = Color.parseColor("#80ffbb33") // FIXME get color holo orange with alpha
            paint.color = debugTextColor
            paint.alpha = 255

            var l = 1f
            val h = 50

//            canvas.drawText("maxScale: $maxScale / minScale: $minScale / density: $density", 8f, l++ * h, paint)
//            canvas.drawText("Norm:${normScale.f(2)}|Scale:${scale.f(2)}", 8f, l++ * h, paint)
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
            // Turn Stats
            canvas.drawText(
                "Turn:${turn!!.f(4)}" +
                        "|As:${GBViewModel.viewShips.size.f(4)}" +
                        "|Ds:${GBViewModel.viewDeepSpaceShips.size.f(4)}" +
                        "|sh:${gi.shots!!.size.f(3)}",
                8f,
                l++ * h,
                paint
            )
            var test = Runtime.getRuntime().totalMemory()
            // Memroy Stats
            canvas.drawText(
                "MM:${(Runtime.getRuntime().maxMemory() / 1048576).f(3)}" +
                        "|TM:${(Runtime.getRuntime().totalMemory() / 1048576).f(3)}" +
                        "|UM:${((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576).f(3)}" +
                        "|FM:${(Runtime.getRuntime().freeMemory() / 1048576).f(3)}",
                8f,
                l++ * h,
                paint
            )
            // Performance Stats
            canvas.drawText(
                "Do:${(GBViewModel.timeLastTurn / 1000000L).f(3)}ms" +
                        "|FW:${(GBViewModel.timeFileWrite / 1000000L).f(2)}ms" +
                        "|FJ:${(GBViewModel.timeFromJson / 1000000L).f(2)}ms" +
                        "|MU:${(GBViewModel.timeModelUpdate / 1000000L).f(2)}ms" +
                        "|Draw:${(last20.average() / 1000000).toInt().f(2)}ms",
                8f,
                l++ * h,
                paint
            )

            GBViewModel.times.forEach { t, u -> canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, paint) }

//            times.forEach { t, u -> canvas.drawText("$t:${(u / 1000L).f(4)}μs", 8f, l++ * h, paint) }

        }
    }


    private fun drawRaces(canvas: Canvas) {
        // Timing Info:  no race 200μs, 1 race 400μs, more ?μs
        if (normScale > 50) {

            for ((_, r) in GBViewModel.viewRaces) {
                if (pointVisible(r.getHome().star.loc.getLoc().x * uToSf, r.getHome().star.loc.getLoc().y * uToSf)) {
                    sP1.set(r.getHome().star.loc.getLoc().x * uToSf + 50, r.getHome().star.loc.getLoc().y * uToSf)
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
        if (40 > normScale) {

            if (true) {
                for (shot: GBVector in GBViewModel.viewShots) {
                    paint.color = shotColor
                    paint.strokeWidth = strokeWidth.toFloat() / 4
                    if (pointVisible(shot.from.x * uToSf, shot.from.y * uToSf) ||
                        pointVisible(shot.to.x * uToSf, shot.to.y * uToSf)
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
        paint.setTextAlign(Paint.Align.CENTER);
        paint.style = Style.FILL
        paint.color = labelColor
        paint.alpha = 128
        for ((_, s) in GBViewModel.viewStars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawText(s.name, vP1.x, vP1.y - 45, paint)
            }
        }
    }

    private fun drawDeepSpaceShips(canvas: Canvas) {
        // Timing Info:  no ships 300μs, 50 ships  2000μs, 500 ships 900μs (at beginning)
        if (101 >= normScale) {
            //for (sh in GBViewModel.viewDeepSpaceShips) {
            for (sh in GBViewModel.viewDeepSpaceShips) {
                if (pointVisible(
                        sh.loc.getLoc().x * uToSf,
                        sh.loc.getLoc().y * uToSf
                    )
                ) {
                    paint.color = Color.parseColor(sh.race.color)
                    paint.alpha = 128
                    drawShip(canvas, sh)
                }
            }
        }
    }

    private fun drawPlanetSurface(canvas: Canvas) {

        if (1 > normScale) {
            for ((_, s) in GBViewModel.viewStars) {
                if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                    for (p in GBViewModel.viewStarPlanets[s.uid]!!) { // PERF only draw one...
                        if (planetVisible(
                                p.loc.getLoc().x * uToSf,
                                p.loc.getLoc().y * uToSf
                            )
                        ) { // FIXME do this in Float

                            sP1.set(p.loc.getLoc().x * uToS, p.loc.getLoc().y * uToS)
                            sourceToViewCoord(sP1, vP1)

                            for (j in 0 until p.sectors.size) {

                                val o = (PlanetaryOrbit * 0.4f) * uToS * scale
                                val size = 4 * o / p.width
                                //canvas.drawBitmap(bitmaps[p.sectors[j].type],p.sectorX(j) * 50f,p.sectorY(j) *50f,null)
                                canvas.drawBitmap(
                                    bmASurface[p.sectors[j].type]!!,
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
                                    val fill = p.sectors[j].population.toFloat() / p.sectors[j].maxPopulation.toFloat()
                                    paint.style = Style.STROKE
                                    paint.color = Color.parseColor(p.sectors[j].sectorOwner.color)
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


    private fun drawPlanetsAndShips(canvas: Canvas) {
        for ((_, s) in GBViewModel.viewStars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {

                for (p in viewStarPlanets[s.uid]!!) {

                    if (30 > normScale) {

                        sP1.set(p.loc.getLoc().x * uToS, p.loc.getLoc().y * uToS)
                        sourceToViewCoord(sP1, vP1)
                        if (normScale > 1) {
                            canvas.drawBitmap(
                                bmPlanet!!,
                                vP1.x - bmPlanet!!.width / 2,
                                vP1.y - bmPlanet!!.height / 2,
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
                            var o = (PlanetaryOrbit * 0.4f) * uToS * scale
                            canvas.drawText(p.name, vP1.x, vP1.y - o * 1.1f, paint)
                            clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y - o * 1.1f), p))
                        }


                        // planet orbit circles and surface rectangles
                        if (3 > normScale) {
                            paint.style = Style.STROKE
                            paint.color = circleColor
                            paint.strokeWidth = strokeWidth.toFloat()
                            val radius = gi.planetaryOrbit * uToS * scale // TODO Constant PLANETARY_ORBIT
                            canvas.drawCircle(vP1.x, vP1.y, radius, paint)

                            val o = (PlanetaryOrbit * 0.4f) * uToS * scale
                            canvas.drawRect(vP1.x - 2 * o, vP1.y - o, vP1.x + 2 * o, vP1.y + o, paint)

                        }
                    } // if scale

                    // Draw orbit ships
                    for (sh in GBViewModel.viewOrbitShips[p.uid]!!) {
                        paint.alpha = 128
                        paint.color = Color.parseColor(sh.race.color)
                        drawShip(canvas, sh)
                    }

                    // Draw landed ships

                    for (sh in GBViewModel.viewLandedShips[p.uid]!!) {
                        paint.alpha = 128
                        paint.color = Color.parseColor(sh.race.color)
                        drawShip(canvas, sh)
                    }

                } // planet loop

                // Draw In System Ship
                for (sh in GBViewModel.viewStarShips[s.uid]!!) {
                    paint.alpha = 255
                    paint.color = Color.parseColor(sh.race.color)
                    drawShip(canvas, sh)
                } // ships loop

            }// if star pointVisible?
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

    fun drawShip(canvas: Canvas, sh: GBShip) {

        paint.style = Style.STROKE
        val radius = scale * 2f

        paint.strokeWidth = strokeWidth.toFloat()

        if (sh.health <= 0) {
            paint.color = deadColor
        }
        // FIXME: Pods turn white when entering orbit. I think they turn dead, and we currently draw all ships, instead
        // of orbit ships, so we get the death flash of the white ones. Maybe this is a feature...

        sP1.set(sh.loc.getLoc().x * uToS, sh.loc.getLoc().y * uToS)
        sourceToViewCoord(sP1, vP1)
        when (sh.idxtype) {

            // TODO: Ships should tell give me the ID of their bitmap and this when statement would go away
            // But GBShips don't know anything about bitmaps, so the logic needs to live elsewhere.

            POD -> {
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
                if (1 > normScale) {
                    if (sh.race == GBViewModel.viewRaces[2]) {
                        canvas.drawBitmap(
                            bitmaps[R.drawable.beetlepod]!!,
                            vP1.x - bitmaps[R.drawable.beetlepod]!!.width / 2,
                            vP1.y - bitmaps[R.drawable.beetlepod]!!.height / 2,
                            null
                        )
                    } else {
                        canvas.drawBitmap(
                            bitmaps[R.drawable.podt]!!,
                            vP1.x - bitmaps[R.drawable.podt]!!.width / 2,
                            vP1.y - bitmaps[R.drawable.podt]!!.height / 2,
                            null
                        )
                    }
                }
            }
            CRUISER -> {
                canvas.drawRect(vP1.x - radius, vP1.y - radius, vP1.x + radius, vP1.y + radius, paint)
                if (1 > normScale) {
                    canvas.drawBitmap(
                        bitmaps[R.drawable.cruisert]!!,
                        vP1.x - bitmaps[R.drawable.cruisert]!!.width / 2,
                        vP1.y - bitmaps[R.drawable.cruisert]!!.height / 2,
                        null
                    )
                }
            }
            FACTORY -> {
                canvas.drawRect(vP1.x - radius, vP1.y - radius, vP1.x + radius, vP1.y + radius, paint)
                if (1 > normScale) {
                    canvas.drawBitmap(
                        bitmaps[R.drawable.factory]!!,
                        vP1.x - bitmaps[R.drawable.factory]!!.width / 2,
                        vP1.y - bitmaps[R.drawable.factory]!!.height / 2,
                        null
                    )
                }
            }
            else -> {
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
        }

        if (5 > normScale) {
            clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y), sh))
        }

        // Don't draw trails zoomed out
        if (normScale > 10) {
            return
        }

        //FIXME PERSISTENCE Fix Trails and turn them back on after restore.

        val trail = viewShipTrails.get(sh.uid) // FIXME

        if (trail != null) {
            paint.strokeWidth = strokeWidth.toFloat() / 2
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Cap.BUTT
            paint.color = trailColor
            val alphaFade = paint.alpha / (trail.size + 1)
            paint.alpha = 0


            val iterate = trail.iterator()

            val from = iterate.next()
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
        pinnedPlanetX = viewPlanets[uidP]!!.loc.getLoc().x
        pinnedPlanetY = viewPlanets[uidP]!!.loc.getLoc().y
    }

    fun unpinPlanet() {
        pinnedUidPlanet = null
    }

    fun shiftToPinnedPlanet() {
        if (pinnedUidPlanet != null) {
            val p = viewPlanets[pinnedUidPlanet!!]!!
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
        paint.style = Style.STROKE
        for ((_, s) in GBViewModel.viewStars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawBitmap(bmStar!!, vP1.x - bmStar!!.getWidth() / 2, vP1.y - bmStar!!.getWidth() / 2, null)

                clickTargets.add(GBClickTarget(PointF(vP1.x, vP1.y), s))

            }
        }

        // Draw circles
        // was if (40 > normScale) {
        paint.style = Style.STROKE
        paint.color = circleColor
        paint.strokeWidth = strokeWidth.toFloat()
        val radius = sSystemSize.toFloat() * scale

        for ((_, s) in GBViewModel.viewStars) {
            if (pointVisible(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)) {
                sP1.set(s.loc.getLoc().x * uToSf, s.loc.getLoc().y * uToSf)
                sourceToViewCoord(sP1, vP1)
                canvas.drawCircle(vP1.x, vP1.y, radius, paint)
            }
        }


    }

    fun drawClickTargets(canvas: Canvas) {

        paint.style = Style.STROKE
        paint.color = Color.parseColor("#1055bb33")
        paint.strokeWidth = strokeWidth.toFloat()
        val radius = 80f

        clickTargets.forEach { canvas.drawCircle(it.center.x, it.center.y, radius, paint) }


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        xClick = event.x
        yClick = event.y

        if (isReady) {
            sClick = viewToSourceCoord(xClick, yClick)!!
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
            if (distance < 80f) {
                return closest.any
            } else {
                val closestPlanet = clickTargets.filter { it.any is GBPlanet }
                    .minBy { (it.center.x - x) * (it.center.x - x) + (it.center.y - y) * (it.center.y - y) }
                if (closestPlanet != null) {
                    val distance2 =
                        sqrt((closest.center.x - x) * (closest.center.x - x) + (closest.center.y - y) * (closest.center.y - y))
                    if (distance2 < gi.planetaryOrbit * uToSf * scale) {
                        return closest.any
                    }
                }
            }
        }
        return null
    }

}
