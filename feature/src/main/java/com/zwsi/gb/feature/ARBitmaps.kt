package com.zwsi.gb.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.zwsi.gblib.GBRace
import kotlin.system.measureNanoTime


fun GBRace.getDrawableResource() : Int {
    return getRaceDrawableResource(this.idx)
}

const val NumberOfRacesWithBitmaps = 6

fun getRaceDrawableResource(idxRace: Int): Int {
    return when (idxRace) {
        0 -> R.drawable.race_xenos
        1 -> R.drawable.race_impi
        2 -> R.drawable.race_beetle
        3 -> R.drawable.race_tortoise
        4 -> R.drawable.race_5
        5 -> R.drawable.race_6
        else -> R.drawable.missing
    }
}

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
            val bmOptions = BitmapFactory.Options()
            bmOptions.inSampleSize = 10

            // Get star and planet
            otherBitmaps[R.drawable.star] =
                BitmapFactory.decodeResource(context.getResources(), R.drawable.star, bmOptions)!!

            val bmPlanet = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_deprecated)!!
            w = density / 420f * bmPlanet.getWidth() / 2
            h = density / 420f * bmPlanet.getHeight() / 2
            otherBitmaps[R.drawable.planet_deprecated] =
                Bitmap.createScaledBitmap(bmPlanet, w.toInt(), h.toInt(), true)!!

            bmOptions.inSampleSize = 10
            initTimes["iPB"] = measureNanoTime {
                planetBitmaps[0] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_mclass, bmOptions)!!
                planetBitmaps[1] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_jovian, bmOptions)!!
                planetBitmaps[2] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_water, bmOptions)!!
                planetBitmaps[3] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_desert, bmOptions)!!
                planetBitmaps[4] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_forest, bmOptions)!!
                planetBitmaps[5] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_ice, bmOptions)!!
                planetBitmaps[6] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_airless, bmOptions)!!
                planetBitmaps[7] =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_asteroid, bmOptions)!!
            }

            initTimes["iPS"] = measureNanoTime {
                surfaceBitmaps[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_desert)!!
                surfaceBitmaps[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_forest)!!
                surfaceBitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_gas)!!
                surfaceBitmaps[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_ice)!!
                surfaceBitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_land)!!
                surfaceBitmaps[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_mountain)!!
                surfaceBitmaps[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.surface_rock)!!
                surfaceBitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.water)!!
            }


            // get Races
            initTimes["iRB"] = measureNanoTime {
                for (i in 0 until NumberOfRacesWithBitmaps) {
                    val bm = BitmapFactory.decodeResource(context.getResources(), getRaceDrawableResource(i))!!
                    w = density / 420f * bm.getWidth() / 30
                    h = density / 420f * bm.getHeight() / 30
                    raceBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            initTimes["iSB"] = measureNanoTime {
                val drawables = listOf<Int>(
                    R.drawable.podt,
                    R.drawable.ship_cruiser,
                    R.drawable.ship_factory,
                    R.drawable.ship_pod_beetle,
                    R.drawable.ship_shuttle,
                    R.drawable.ship_research,
                    R.drawable.ship_hq,
                    R.drawable.ship_battlestar,
                    R.drawable.ship_wheel
                )
                for (i in drawables) {
                    val bm = BitmapFactory.decodeResource(context.getResources(), i)!!
                    w = density / 420f * bm.getWidth() / 6
                    h = density / 420f * bm.getHeight() / 6
                    shipBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            initTimes["iRB"] = measureNanoTime {
                for (i in 0 until numberOfFrames) {
                    wheelBitmaps[i] = shipBitmaps[R.drawable.ship_wheel]!!.rotate((360.toFloat() / numberOfFrames * i))
                }
            }
            ready = true
        }
    }
}