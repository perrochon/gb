package com.zwsi.gb.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.system.measureNanoTime

class ARBitmaps {
    companion object {

        var ready = false;

        private val planetBitmaps = HashMap<Int, Bitmap>()
        private val surfaceBitmaps = HashMap<Int, Bitmap>()
        private val otherBitmaps = HashMap<Int, Bitmap>()
        private val raceBitmaps = HashMap<Int, Bitmap>()
        private val shipBitmaps = HashMap<Int, Bitmap>()

        val numberOfFrames = 150 // 10s rotation, 30fps -> up to 300 different angles/shipBitmaps! 600 @60fps
        var wheelBitmaps = arrayOfNulls<Bitmap>(numberOfFrames)

        var initTimes = mutableMapOf<String, Long>()

        // FIXME in all of these, check for ready, and if not ready, return a default bitmap

        fun planetBitmap(id: Int): Bitmap {
            return planetBitmaps[id]!!
        }

        fun surfaceBitmap(id: Int): Bitmap {
            return surfaceBitmaps[id]!!
        }

        fun otherBitmap(id: Int): Bitmap {
            return otherBitmaps[id]!!
        }

        fun raceBitmap(id: Int): Bitmap {
            return raceBitmaps[id]!!
        }

        fun shipBitmap(id: Int): Bitmap {
            return shipBitmaps[id]!!
        }

        fun wheelBitmap(id: Int): Bitmap {
            return wheelBitmaps[id]!!
        }

        fun loadBitmaps(context: Context) {
            // Do a better way. If it works, we replace above... Neet do figure out planet/star, where we divide by 2/1

            var h: Float
            var w: Float

            val density = context.resources.displayMetrics.densityDpi.toFloat()
            var bmOptions = BitmapFactory.Options()
            bmOptions.inSampleSize = 10

            // Get star and planet
            otherBitmaps[R.drawable.star] = BitmapFactory.decodeResource(context.getResources(), R.drawable.star, bmOptions)!!

            val bmPlanet = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_deprecated)!!
            w = density / 420f * bmPlanet.getWidth() / 2
            h = density / 420f * bmPlanet.getHeight() / 2
            otherBitmaps[R.drawable.planet_deprecated] = Bitmap.createScaledBitmap(bmPlanet, w.toInt(), h.toInt(), true)!!

            bmOptions.inSampleSize = 10
            initTimes["iPB"] = measureNanoTime {
                planetBitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_mclass, bmOptions)!!
                planetBitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_jovian, bmOptions)!!
                planetBitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_water, bmOptions)!!
                planetBitmaps[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_desert, bmOptions)!!
                planetBitmaps[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_forest, bmOptions)!!
                planetBitmaps[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_ice, bmOptions)!!
                planetBitmaps[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_airless, bmOptions)!!
                planetBitmaps[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_asteroid, bmOptions)!!
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
                val drawables = listOf<Int>(
                    R.drawable.race_xenos, R.drawable.race_impi, R.drawable.race_beetle, R.drawable.race_tortoise, R.drawable.race_5, R.drawable.race_6
                )
                for (i in drawables) {
                    val bm = BitmapFactory.decodeResource(context.getResources(), i)!!
                    w = density / 420f * bm.getWidth() / 30
                    h = density / 420f * bm.getHeight() / 30
                    raceBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            initTimes["iSB"] = measureNanoTime {
                val drawables = listOf<Int>(
                    R.drawable.podt, R.drawable.ship_cruiser, R.drawable.ship_factory, R.drawable.ship_pod_beetle,
                    R.drawable.ship_shuttle, R.drawable.ship_research, R.drawable.ship_hq, R.drawable.ship_battlestar, R.drawable.ship_wheel
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