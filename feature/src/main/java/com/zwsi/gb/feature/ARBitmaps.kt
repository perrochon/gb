package com.zwsi.gb.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.zwsi.gb.feature.ARBitmaps.Companion.planetBitmap
import com.zwsi.gb.feature.ARBitmaps.Companion.raceBitmap
import com.zwsi.gb.feature.ARBitmaps.Companion.shipBitmap
import com.zwsi.gb.feature.ARBitmaps.Companion.wheelBitmap
import com.zwsi.gblib.GBData
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBRace
import com.zwsi.gblib.GBShip
import kotlin.system.measureNanoTime

const val NumberOfRacesWithBitmaps = 6

val race_colors = longArrayOf(0xffe34234, 0xff009e73, 0xffcc79a7, 0xff56b4e9, 0xff0072b2, 0xffF0E442)

fun GBRace.getColor(): Int {
    return race_colors[this.idx].toInt()
}

fun GBRace.getDrawableResource(): Int {
    return getRaceDrawableResource(this.idx)
}

fun GBRace.getBitmap(): Bitmap {
    return raceBitmap(this.idx)
}

fun getRaceDrawableResource(idx: Int): Int {
    return when (idx) {
        0 -> R.drawable.race_xenos
        1 -> R.drawable.race_impi
        2 -> R.drawable.race_beetle
        3 -> R.drawable.race_tortoise
        4 -> R.drawable.race_ghosts
        5 -> R.drawable.race_tools
        else -> R.drawable.missing
    }
}

const val NumberOfShipsWithBitmaps = 8
const val NumberOfShipsWithAlternativeBitmaps = 1 // FIXME Better handling of alternative ship bitmaps

fun GBShip.getDrawableResource(): Int {
    return getShipDrawableResource(this.idxtype)
}

fun GBShip.getBitmap(): Bitmap {
    if (this.idxtype == GBData.POD && this.uidRace == 2) {
        return shipBitmap(NumberOfShipsWithBitmaps) // FIXME Better handling of alternative ship bitmaps
    } else if (this.idxtype == GBData.STATION) {
        val i = (System.currentTimeMillis().rem(10000).div((10000 / ARBitmaps.numberOfFrames)).toInt()
                + this.uid).rem(ARBitmaps.numberOfFrames)
        return wheelBitmap(i)
    } else {
        return shipBitmap(this.idxtype)
    }
}

fun getShipDrawableResource(idx: Int): Int {
    return when (idx) {
        GBData.POD -> R.drawable.podt
        GBData.CRUISER -> R.drawable.ship_cruiser
        GBData.FACTORY -> R.drawable.ship_factory
        GBData.SHUTTLE -> R.drawable.ship_shuttle
        GBData.RESEARCH -> R.drawable.ship_research
        GBData.HEADQUARTERS -> R.drawable.ship_hq
        GBData.BATTLESTAR -> R.drawable.ship_battlestar
        GBData.STATION -> R.drawable.ship_wheel
        GBData.STATION + 1 -> R.drawable.ship_pod_beetle // FIXME Better handling of alternative ship bitmaps
        else -> R.drawable.missing
    }
}

fun GBPlanet.getBitmap(): Bitmap {
    return planetBitmap(this.idxtype)
}

fun GBPlanet.getDrawableResource(): Int {
    return getPlanetDrawableResource(this.idxtype)
}

fun getPlanetDrawableResource(idx: Int): Int {
    return when (idx) {
        0 -> R.drawable.planet_mclass
        1 -> R.drawable.planet_jovian
        2 -> R.drawable.planet_water
        3 -> R.drawable.planet_desert
        4 -> R.drawable.planet_forest
        5 -> R.drawable.planet_ice
        6 -> R.drawable.planet_airless
        7 -> R.drawable.planet_asteroid
        else -> R.drawable.missing
    }
}


// FIXME Should functions that are not extensions move into the class?

class ARBitmaps {
    companion object {

        var ready = false;

        private val planetBitmaps = HashMap<Int, Bitmap>()
        private val surfaceBitmaps = HashMap<Int, Bitmap>()
        private val otherBitmaps = HashMap<Int, Bitmap>()
        private val raceBitmaps = HashMap<Int, Bitmap>()
        private val shipBitmaps = HashMap<Int, Bitmap>()
        private val defaultBitMap = BitmapFactory.decodeResource(GBViewModel.context!!.resources, R.drawable.missing);

        val numberOfFrames = 150 // 10s rotation, 30fps -> up to 300 different angles/shipBitmaps! 600 @60fps
        var wheelBitmaps = arrayOfNulls<Bitmap>(numberOfFrames)

        var initTimes = mutableMapOf<String, Long>()

        // These will be only called from one place, so maybe get rid of functions?

        fun planetBitmap(id: Int): Bitmap {
            return if (ready) planetBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun surfaceBitmap(id: Int): Bitmap {
            return if (ready) surfaceBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun otherBitmap(id: Int): Bitmap {
            return if (ready) otherBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun raceBitmap(id: Int): Bitmap {
            return if (ready) raceBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun shipBitmap(id: Int): Bitmap {
            return if (ready) shipBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun wheelBitmap(id: Int): Bitmap {
            return if (ready) wheelBitmaps[id] ?: defaultBitMap else defaultBitMap
        }

        fun loadBitmaps(context: Context) {
            // Do a better way. If it works, we replace above... Neet do figure out planet/star, where we divide by 2/1

            var h: Float
            var w: Float

            val density = context.resources.displayMetrics.densityDpi.toFloat()

            // Get star and planet
            otherBitmaps[R.drawable.star] =
                BitmapFactory.decodeResource(context.resources, R.drawable.star)!!

            initTimes["iPB"] = measureNanoTime {
                for (i in 0..7) {
                    planetBitmaps[i] =
                        BitmapFactory.decodeResource(context.resources, getPlanetDrawableResource(i))!!
                }
            }

            initTimes["iPS"] = measureNanoTime {
                surfaceBitmaps[3] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_desert)!!
                surfaceBitmaps[5] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_forest)!!
                surfaceBitmaps[2] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_gas)!!
                surfaceBitmaps[6] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_ice)!!
                surfaceBitmaps[1] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_land)!!
                surfaceBitmaps[4] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_mountain)!!
                surfaceBitmaps[7] = BitmapFactory.decodeResource(context.resources, R.drawable.surface_rock)!!
                surfaceBitmaps[0] = BitmapFactory.decodeResource(context.resources, R.drawable.water)!!
            }

            // get Races
            initTimes["iRB"] = measureNanoTime {
                for (i in 0 until NumberOfRacesWithBitmaps) {
                    val bm = BitmapFactory.decodeResource(context.resources, getRaceDrawableResource(i))!!
                    w = density / 420f * bm.getWidth() / 30
                    h = density / 420f * bm.getHeight() / 30
                    raceBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            // get Ships
            initTimes["iSB"] = measureNanoTime {
                for (i in 0..NumberOfShipsWithBitmaps + NumberOfShipsWithAlternativeBitmaps) {
                    val bm = BitmapFactory.decodeResource(context.resources, getShipDrawableResource(i))!!
                    w = density / 420f * bm.getWidth() / 6
                    h = density / 420f * bm.getHeight() / 6
                    shipBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            // compute wheel animations
            initTimes["iWB"] = measureNanoTime {
                for (i in 0 until numberOfFrames) {
                    wheelBitmaps[i] = shipBitmaps[GBData.STATION]!!.rotate((360.toFloat() / numberOfFrames * i))
                }
            }
            ready = true
        }
    }
}